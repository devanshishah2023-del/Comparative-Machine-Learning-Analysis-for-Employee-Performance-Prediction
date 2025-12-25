<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String role = (String) session.getAttribute("role");
    if (!"ADMIN".equals(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Model History - Employee Performance Analytics</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .container { max-width: 1200px; margin: 20px auto; padding: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; background: white; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background: #667eea; color: white; font-weight: 600; }
        tr:hover { background: #f5f5f5; }
        .no-data { text-align: center; padding: 40px; background: white; border-radius: 8px; margin-top: 20px; }
        .btn { padding: 10px 20px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 15px; }
        .btn:hover { background: #5568d3; }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="nav-brand">Employee Performance Analytics</div>
        <div class="nav-menu">
            <a href="dashboard.jsp">Dashboard</a>
            <a href="admin.jsp">Admin</a>
            <a href="model-history.jsp" class="active">Model History</a>
            <a href="LogoutServlet">Logout</a>
        </div>
    </nav>

    <div class="container">
        <h1>Model Training History</h1>
        <div id="modelHistory"></div>
    </div>
    
    <script>
        function loadModelHistory() {
            var historyDiv = document.getElementById('modelHistory');
            historyDiv.innerHTML = '<p style="text-align: center; padding: 40px;">Loading...</p>';
            
            fetch('/performance-analytics/api/models')
                .then(function(response) { return response.text(); })
                .then(function(text) {
                    console.log('Response:', text);
                    var data = JSON.parse(text);
                    
                    if (!data.models || data.models.length === 0) {
                        historyDiv.innerHTML = 
                            '<div class="no-data">' +
                            '<h3>No Training History Yet</h3>' +
                            '<p>Train a model from the Admin page to see results here.</p>' +
                            '<a href="admin.jsp" class="btn">Go to Admin Page</a>' +
                            '</div>';
                    } else {
                        var html = '<table><thead><tr>';
                        html += '<th>Algorithm</th><th>Accuracy</th><th>Precision</th><th>Recall</th>';
                        html += '<th>F1-Score</th><th>Kappa</th><th>Date</th><th>Trained By</th>';
                        html += '</tr></thead><tbody>';
                        
                        data.models.forEach(function(model) {
                            html += '<tr>';
                            html += '<td><strong>' + model.algorithm + '</strong></td>';
                            html += '<td>' + model.accuracy.toFixed(2) + '%</td>';
                            html += '<td>' + model.precision.toFixed(2) + '%</td>';
                            html += '<td>' + model.recall.toFixed(2) + '%</td>';
                            html += '<td>' + model.fMeasure.toFixed(2) + '%</td>';
                            html += '<td>' + model.kappa.toFixed(4) + '</td>';
                            html += '<td>' + model.trainingDate + '</td>';
                            html += '<td>' + model.trainedBy + '</td>';
                            html += '</tr>';
                        });
                        
                        html += '</tbody></table>';
                        historyDiv.innerHTML = html;
                    }
                })
                .catch(function(error) {
                    console.error('Error:', error);
                    historyDiv.innerHTML = 
                        '<div class="no-data"><p>Error loading history. Please try again.</p></div>';
                });
        }
        
        window.onload = loadModelHistory;
    </script>
</body>
</html>
