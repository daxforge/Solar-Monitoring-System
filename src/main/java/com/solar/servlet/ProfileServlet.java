package com.solar.servlet;

import com.solar.exception.ValidationException;
import com.solar.model.User;
import com.solar.service.AuthService;
import com.solar.service.UserService;
import com.solar.util.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller managing personal user profile and security credential updates.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = "/profile")
public class ProfileServlet extends HttpServlet {

    private UserService userService;
    private AuthService authService;

    @Override
    public void init() {
        this.userService = new UserService();
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);

        // Fetch fresh user record
        User user = userService.getUserById(currentUser.getId());
        req.setAttribute("user", user);

        req.getRequestDispatcher("/WEB-INF/views/homeowner/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);
        String action = req.getParameter("action");

        try {
            if ("updateInfo".equalsIgnoreCase(action)) {
                String name = req.getParameter("name");
                String email = req.getParameter("email");

                userService.updateProfile(currentUser.getId(), name, email);

                // Update session state
                User updatedUser = userService.getUserById(currentUser.getId());
                session.setAttribute(Constants.SESSION_USER, updatedUser);

                session.setAttribute(Constants.FLASH_SUCCESS, "Profile updated successfully.");

            } else if ("changePassword".equalsIgnoreCase(action)) {
                String oldPassword = req.getParameter("oldPassword");
                String newPassword = req.getParameter("newPassword");
                String confirmPassword = req.getParameter("confirmPassword");

                if (!newPassword.equals(confirmPassword)) {
                    throw new ValidationException("New password and confirmation do not match.");
                }

                authService.changePassword(currentUser.getId(), oldPassword, newPassword);
                session.setAttribute(Constants.FLASH_SUCCESS, "Password changed successfully.");
            }
        } catch (ValidationException ve) {
            session.setAttribute(Constants.FLASH_ERROR, ve.getMessage());
        } catch (Exception e) {
            session.setAttribute(Constants.FLASH_ERROR, "Failed to update profile: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}
