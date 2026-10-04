package com.solar.servlet;

import com.solar.enums.Role;
import com.solar.exception.AuthenticationException;
import com.solar.model.User;
import com.solar.service.AuthService;
import com.solar.util.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller managing user authentication, login view rendering, and Post-Redirect-Get session initialization.
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() {
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constants.SESSION_USER) != null) {
            User user = (User) session.getAttribute(Constants.SESSION_USER);
            if (user.getRole() == Role.ADMIN) {
                resp.sendRedirect(req.getContextPath() + Constants.URL_ADMIN_DASHBOARD);
            } else {
                resp.sendRedirect(req.getContextPath() + Constants.URL_HOMEOWNER_DASHBOARD);
            }
            return;
        }

        // Check if redirected after logout
        if ("true".equals(req.getParameter("logout"))) {
            req.setAttribute("logoutMessage", "You have been logged out securely.");
        }

        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = authService.authenticate(email, password);

            // Create new session to guard against session fixation
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setAttribute(Constants.SESSION_USER, user);
            session.setAttribute(Constants.FLASH_SUCCESS, "Welcome back, " + user.getName() + "!");

            // Check if there was an intended URL prior to login redirect
            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                session.removeAttribute("redirectAfterLogin");
                resp.sendRedirect(req.getContextPath() + redirectUrl);
                return;
            }

            // Post-Redirect-Get pattern by role
            if (user.getRole() == Role.ADMIN) {
                resp.sendRedirect(req.getContextPath() + Constants.URL_ADMIN_DASHBOARD);
            } else {
                resp.sendRedirect(req.getContextPath() + Constants.URL_HOMEOWNER_DASHBOARD);
            }

        } catch (AuthenticationException ae) {
            req.setAttribute("errorMessage", ae.getMessage());
            req.setAttribute("enteredEmail", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "An unexpected error occurred during login. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }
}
