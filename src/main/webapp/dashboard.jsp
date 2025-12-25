<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String role = (String) session.getAttribute("role");
    String username = (String) session.getAttribute("username");
    
    if (role == null || username == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Employee Performance Analytics</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        .header {
            background: white;
            padding: 20px 40px;
            border-radius: 10px;
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        .header h1 {
            color: #667eea;
            font-size: 1.8em;
        }
        .user-info {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        .user-info span {
            color: #555;
            font-weight: 600;
        }
        .user-info .role {
            background: #667eea;
            color: white;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 0.9em;
        }
        .logout-btn {
            padding: 10px 20px;
            background: #dc3545;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-weight: bold;
        }
        .logout-btn:hover {
            background: #c82333;
        }
        .container {
            background: white;
            padding: 40px;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.2);
            max-width: 1200px;
            margin: 0 auto;
        }
        .menu-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .menu-btn {
            padding: 30px 20px;
            font-size: 1.1em;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            transition: all 0.3s;
            font-weight: bold;
            text-align: center;
            text-decoration: none;
            display: block;
        }
        .menu-btn:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.2);
        }
        .admin-btn {
            background: #667eea;
            color: white;
        }
        .user-btn {
            background: #764ba2;
            color: white;
        }
        .history-btn {
            background: #28a745;
            color: white;
        }
        .model-btn {
            background: #17a2b8;
            color: white;
        }
        .about-btn {
            background: #ffc107;
            color: #333;
        }
        .soap-btn {
            background: #6c757d;
            color: white;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>Employee Performance Analytics</h1>
        <div class="user-info">
            <span>Welcome, <strong><%= username %></strong></span>
            <span class="role"><%= role %></span>
            <a href="logout" class="logout-btn">Logout</a>
        </div>
    </div>
    
    <div class="container">
        <div class="menu-grid">
            <% if ("ADMIN".equals(role)) { %>
                <a href="admin.jsp" class="menu-btn admin-btn">
                    Admin Dashboard<br>
                    <small>Compare & Train Models</small>
                </a>
                <a href="model-history.jsp" class="menu-btn model-btn">
                    Model History<br>
                    <small>View Past Models</small>
                </a>
            <% } %>
            
            <a href="predict.jsp" class="menu-btn user-btn">
                Make Prediction<br>
                <small>Predict Performance</small>
            </a>
            
            <a href="prediction-history.jsp" class="menu-btn history-btn">
                Prediction History<br>
                <small>View Past Predictions</small>
            </a>
            
            <a href="about.jsp" class="menu-btn about-btn">
                About Us<br>
                <small>Project Information</small>
            </a>
            
            <% if ("ADMIN".equals(role)) { %>
                <a href="soap-test.jsp" class="menu-btn soap-btn">
                    SOAP Services<br>
                    <small>Web Services</small>
                </a>
            <% } %>
        </div>
    </div>
</body>
</html>
