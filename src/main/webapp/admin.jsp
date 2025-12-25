<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String role = (String) session.getAttribute("role");
    if (role == null || !role.equals("ADMIN")) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Employee Performance Analytics</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .navbar {
            background: white;
            padding: 15px 30px;
            border-radius: 15px;
            margin-bottom: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .nav-brand {
            font-size: 24px;
            font-weight: bold;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }
        
        .nav-menu {
            display: flex;
            gap: 20px;
        }
        
        .nav-menu a {
            text-decoration: none;
            color: #333;
            padding: 8px 16px;
            border-radius: 8px;
            transition: all 0.3s;
            font-weight: 500;
        }
        
        .nav-menu a:hover {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .nav-menu a.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .container {
            max-width: 1400px;
            margin: 0 auto;
        }
        
        .card {
            background: white;
            border-radius: 20px;
            padding: 40px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            margin-bottom: 30px;
        }
        
        h1 {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            font-size: 36px;
            margin-bottom: 30px;
        }
        
        h3 {
            color: #667eea;
            margin: 30px 0 15px 0;
            font-size: 22px;
        }
        
        .btn {
            padding: 18px 40px;
            border: none;
            border-radius: 12px;
            font-size: 18px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s;
            width: 100%;
            margin-bottom: 20px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.15);
        }
        
        .btn-success {
            background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
            color: white;
        }
        
        .btn-success:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 25px rgba(17, 153, 142, 0.4);
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 25px rgba(79, 172, 254, 0.4);
        }
        
        .form-control {
            width: 100%;
            padding: 15px;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            font-size: 16px;
            margin-bottom: 20px;
            transition: all 0.3s;
        }
        
        .form-control:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .loading {
            text-align: center;
            padding: 40px;
            color: #667eea;
            font-size: 18px;
            background: linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%);
            border-radius: 15px;
        }
        
        .error {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            padding: 20px;
            border-radius: 15px;
            margin: 20px 0;
        }
        
        .error h3 {
            color: white;
            margin-top: 0;
        }
        
        .comparison-header {
            background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
            padding: 25px;
            border-radius: 15px;
            margin-bottom: 25px;
        }
        
        .comparison-header h3 {
            color: #333;
            margin: 0 0 10px 0;
        }
        
        .comparison-header p {
            color: #555;
            margin: 5px 0;
            font-size: 16px;
        }
        
        .metrics-table {
            overflow-x: auto;
            margin: 25px 0;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }
        
        thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        th {
            color: white;
            padding: 15px;
            text-align: left;
            font-weight: 600;
            font-size: 14px;
        }
        
        td {
            padding: 15px;
            border-bottom: 1px solid #f0f0f0;
        }
        
        tbody tr:hover {
            background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 10%);
        }
        
        tbody tr td:first-child {
            font-weight: bold;
            color: #667eea;
        }
        
        .training-header {
            background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
            padding: 25px;
            border-radius: 15px;
            margin-bottom: 25px;
        }
        
        .training-header h3 {
            color: #333;
            margin: 0 0 10px 0;
        }
        
        .metrics-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin: 25px 0;
        }
        
        .metric-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 25px;
            border-radius: 15px;
            text-align: center;
            color: white;
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
            transition: all 0.3s;
        }
        
        .metric-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 12px 30px rgba(102, 126, 234, 0.4);
        }
        
        .metric-card h4 {
            font-size: 14px;
            margin-bottom: 10px;
            opacity: 0.9;
        }
        
        .metric-value {
            font-size: 32px;
            font-weight: bold;
            margin: 0;
        }
        
        #comparisonChart {
            max-height: 400px;
            margin: 30px 0;
            background: white;
            padding: 20px;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        
        .hint {
            font-size: 14px;
            color: rgba(255,255,255,0.9);
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="nav-brand">Employee Performance Analytics</div>
        <div class="nav-menu">
            <a href="dashboard.jsp">Dashboard</a>
            <a href="admin.jsp" class="active">Admin</a>
            <a href="model-history.jsp">Model History</a>
            <a href="prediction-history.jsp">Predictions</a>
            <a href="LogoutServlet">Logout</a>
        </div>
    </nav>

    <div class="container">
        <div class="card">
            <h1>Model Management</h1>
            
            <button onclick="compareAlgorithms()" class="btn btn-success">
                Compare Algorithms
            </button>
            
            <div id="comparisonResults"></div>
            <canvas id="comparisonChart"></canvas>
            
            <h3>Select Algorithm to Train:</h3>
            <select id="algorithmSelect" class="form-control">
                <option value="J48 Decision Tree">J48 Decision Tree</option>
                <option value="Random Forest">Random Forest</option>
                <option value="Naive Bayes">Naive Bayes</option>
            </select>
            
            <button onclick="trainModel()" class="btn btn-primary">
                Train Model
            </button>
            
            <div id="trainResults"></div>
        </div>
    </div>

    <script src="js/admin.js"></script>
</body>
</html>
