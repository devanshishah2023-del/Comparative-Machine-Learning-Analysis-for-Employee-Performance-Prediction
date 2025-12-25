<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Prediction History - Employee Performance Analytics</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding-bottom: 40px;
        }
        
        .navbar {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            padding: 1rem 2rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            position: sticky;
            top: 0;
            z-index: 1000;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .navbar h4 {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            font-weight: 700;
            font-size: 20px;
        }
        
        .navbar a {
            color: #4a5568;
            text-decoration: none;
            margin-left: 24px;
            font-weight: 500;
            font-size: 14px;
            transition: color 0.3s;
            position: relative;
        }
        
        .navbar a:hover {
            color: #667eea;
        }
        
        .navbar a::after {
            content: '';
            position: absolute;
            bottom: -4px;
            left: 0;
            width: 0;
            height: 2px;
            background: #667eea;
            transition: width 0.3s;
        }
        
        .navbar a:hover::after {
            width: 100%;
        }
        
        .container {
            max-width: 1400px;
            margin: 40px auto;
            padding: 0 20px;
        }
        
        .header-card {
            background: white;
            border-radius: 16px;
            padding: 32px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
            margin-bottom: 24px;
        }
        
        .header-card h2 {
            font-size: 32px;
            font-weight: 700;
            color: #1a202c;
            margin-bottom: 8px;
        }
        
        .header-card p {
            color: #718096;
            font-size: 16px;
        }
        
        .stats-row {
            display: flex;
            gap: 16px;
            margin-bottom: 24px;
        }
        
        .stat-card {
            flex: 1;
            background: white;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            display: flex;
            align-items: center;
            gap: 16px;
        }
        
        .stat-icon {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
        }
        
        .stat-icon.high { background: #dcfce7; }
        .stat-icon.medium { background: #fef3c7; }
        .stat-icon.low { background: #fee2e2; }
        
        .stat-content h3 {
            font-size: 24px;
            font-weight: 700;
            color: #1a202c;
        }
        
        .stat-content p {
            font-size: 14px;
            color: #718096;
        }
        
        .table-card {
            background: white;
            border-radius: 16px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
            overflow: hidden;
        }
        
        .table-wrapper {
            overflow-x: auto;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        thead th {
            padding: 16px 20px;
            text-align: left;
            font-size: 13px;
            font-weight: 600;
            color: white;
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }
        
        tbody tr {
            border-bottom: 1px solid #e2e8f0;
            transition: all 0.2s;
        }
        
        tbody tr:hover {
            background: #f7fafc;
            transform: translateX(4px);
            box-shadow: -4px 0 0 #667eea;
        }
        
        tbody td {
            padding: 16px 20px;
            font-size: 14px;
            color: #4a5568;
        }
        
        tbody td:first-child {
            font-weight: 600;
            color: #2d3748;
        }
        
        .badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-weight: 600;
            font-size: 12px;
            display: inline-block;
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }
        
        .badge-success {
            background: #dcfce7;
            color: #166534;
        }
        
        .badge-warning {
            background: #fef3c7;
            color: #92400e;
        }
        
        .badge-danger {
            background: #fee2e2;
            color: #991b1b;
        }
        
        .alert {
            padding: 16px 20px;
            margin: 20px;
            border-radius: 12px;
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 12px;
        }
        
        .alert-info {
            background: #dbeafe;
            color: #1e40af;
            border-left: 4px solid #3b82f6;
        }
        
        .alert-danger {
            background: #fee2e2;
            color: #991b1b;
            border-left: 4px solid #dc2626;
        }
        
        .alert-success {
            background: #dcfce7;
            color: #166534;
            border-left: 4px solid #16a34a;
        }
        
        .loading-spinner {
            display: inline-block;
            width: 20px;
            height: 20px;
            border: 3px solid rgba(59, 130, 246, 0.3);
            border-top-color: #3b82f6;
            border-radius: 50%;
            animation: spin 0.8s linear infinite;
        }
        
        @keyframes spin {
            to { transform: rotate(360deg); }
        }
        
        .footer-info {
            text-align: center;
            color: white;
            margin-top: 32px;
            font-size: 14px;
            opacity: 0.9;
        }
        
        @media (max-width: 768px) {
            .stats-row {
                flex-direction: column;
            }
            
            .navbar {
                flex-direction: column;
                gap: 16px;
            }
            
            .navbar a {
                margin: 0 12px;
            }
            
            thead th, tbody td {
                padding: 12px 16px;
                font-size: 12px;
            }
        }
        
        .fade-in {
            animation: fadeIn 0.5s ease-in;
        }
        
        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <h4>Employee Performance Analytics</h4>
        <div>
            <a href="dashboard.jsp">Dashboard</a>
            <a href="admin.jsp">Admin</a>
            <a href="model-history.jsp">Model History</a>
            <a href="prediction-history.jsp">Prediction History</a>
            <a href="logout">Logout</a>
        </div>
    </nav>

    <div class="container">
        <div class="header-card fade-in">
            <h2>Prediction History</h2>
            <p>View all employee performance predictions with detailed analytics</p>
        </div>
        
        <div id="statsRow" class="stats-row" style="display: none;">
            <div class="stat-card fade-in">
                <div class="stat-icon high"></div>
                <div class="stat-content">
                    <h3 id="highCount">0</h3>
                    <p>High Performance</p>
                </div>
            </div>
            <div class="stat-card fade-in">
                <div class="stat-icon medium"></div>
                <div class="stat-content">
                    <h3 id="mediumCount">0</h3>
                    <p>Medium Performance</p>
                </div>
            </div>
            <div class="stat-card fade-in">
                <div class="stat-icon low"></div>
                <div class="stat-content">
                    <h3 id="lowCount">0</h3>
                    <p>Low Performance</p>
                </div>
            </div>
        </div>
        
        <div id="loading" class="alert alert-info">
            <span class="loading-spinner"></span>
            Loading predictions...
        </div>
        
        <div id="error" class="alert alert-danger" style="display: none;"></div>
        
        <div class="table-card fade-in" id="tableCard" style="display: none;">
            <div class="table-wrapper">
                <table id="predictionsTable">
                    <thead>
                        <tr>
                            <th>Employee ID</th>
                            <th>Performance</th>
                            <th>Confidence</th>
                            <th>Department</th>
                            <th>Date & Time</th>
                        </tr>
                    </thead>
                    <tbody id="tableBody">
                    </tbody>
                </table>
            </div>
        </div>
        
        <p class="footer-info" id="footerInfo" style="display: none;">
            Showing <strong id="totalCount">0</strong> predictions
        </p>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        $(document).ready(function() {
            $.ajax({
                url: '/performance-analytics/api/predictions',
                method: 'GET',
                dataType: 'json',
                success: function(response) {
                    $('#loading').hide();
                    
                    var data = response.predictions || response;
                    
                    if (!data || data.length === 0) {
                        $('#error')
                            .html('No predictions found. <a href="predict-form.jsp" style="color: #991b1b; font-weight: 600;">Make your first prediction</a>')
                            .show();
                        return;
                    }
                    
                    // Count performance levels
                    var highCount = 0, mediumCount = 0, lowCount = 0;
                    
                    data.forEach(function(pred) {
                        var performance = (pred.predictedPerformance || '').toLowerCase();
                        if (performance === 'high') highCount++;
                        else if (performance === 'medium') mediumCount++;
                        else if (performance === 'low') lowCount++;
                        
                        var badgeClass = 'badge-secondary';
                        if (performance === 'high') badgeClass = 'badge-success';
                        else if (performance === 'medium') badgeClass = 'badge-warning';
                        else if (performance === 'low') badgeClass = 'badge-danger';
                        
                        var employeeId = pred.employeeId || 'N/A';
                        var predictedPerf = pred.predictedPerformance || 'Unknown';
                        var confidence = pred.confidenceScore || 0;
                        var department = pred.department || 'N/A';
                        var date = pred.predictionDate || 'N/A';
                        
                        var confStr = confidence.toFixed(1) + '%';
                        
                        var row = '<tr>' +
                            '<td>' + employeeId + '</td>' +
                            '<td><span class="badge ' + badgeClass + '">' + predictedPerf + '</span></td>' +
                            '<td>' + confStr + '</td>' +
                            '<td>' + department + '</td>' +
                            '<td>' + date + '</td>' +
                            '</tr>';
                        
                        $('#tableBody').append(row);
                    });
                    
                    // Update stats
                    $('#highCount').text(highCount);
                    $('#mediumCount').text(mediumCount);
                    $('#lowCount').text(lowCount);
                    $('#totalCount').text(data.length);
                    
                    // Show elements
                    $('#statsRow').show();
                    $('#tableCard').show();
                    $('#footerInfo').show();
                },
                error: function(xhr, status, error) {
                    $('#loading').hide();
                    $('#error').html('Error loading predictions: ' + error).show();
                }
            });
        });
    </script>
</body>
</html>
