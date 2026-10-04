package com.solar.servlet;

import com.solar.enums.Role;
import com.solar.exception.ValidationException;
import com.solar.model.User;
import com.solar.service.UserService;
import com.solar.util.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Administrative Controller for User Management CRUD operations.
 * Demonstrates the Post-Redirect-Get (PRG) pattern and flash messaging.
 */
@WebServlet(name = "UserServlet", urlPatterns = "/admin/users")
public class UserServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("q");
        List<User> users = userService.searchUsers(query);

        req.setAttribute("users", users);
        req.setAttribute("searchQuery", query);
        req.setAttribute("roles", Role.values());

        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String action = req.getParameter("action");

        try {
            if ("create".equalsIgnoreCase(action)) {
                String name = req.getParameter("name");
                String email = req.getParameter("email");
                String password = req.getParameter("password");
                String roleStr = req.getParameter("role");

                Role role = Role.fromString(roleStr);
                userService.createUser(name, email, password, role);
                session.setAttribute(Constants.FLASH_SUCCESS, "User account for '" + name + "' created successfully.");

            } else if ("update".equalsIgnoreCase(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                String name = req.getParameter("name");
                String email = req.getParameter("email");
                Role role = Role.fromString(req.getParameter("role"));
                boolean active = "true".equalsIgnoreCase(req.getParameter("active"));

                userService.updateUser(id, name, email, role, active);
                session.setAttribute(Constants.FLASH_SUCCESS, "User '" + name + "' updated successfully.");

            } else if ("delete".equalsIgnoreCase(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                User currentUser = (User) session.getAttribute(Constants.SESSION_USER);
                if (currentUser != null && currentUser.getId().equals(id)) {
                    session.setAttribute(Constants.FLASH_ERROR, "Cannot delete currently logged-in administrator account.");
                } else {
                    userService.deleteUser(id);
                    session.setAttribute(Constants.FLASH_SUCCESS, "User and associated device bindings removed.");
                }
            }
        } catch (ValidationException ve) {
            session.setAttribute(Constants.FLASH_ERROR, ve.getMessage());
        } catch (Exception e) {
            session.setAttribute(Constants.FLASH_ERROR, "Failed to process user action: " + e.getMessage());
        }

        // Post-Redirect-Get pattern
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}
