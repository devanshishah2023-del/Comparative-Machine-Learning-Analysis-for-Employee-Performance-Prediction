<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Employee Performance Analytics System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        .container {
            background: white;
            padding: 40px;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.2);
            max-width: 1000px;
            width: 100%;
        }
        h1 {
            color: #667eea;
            text-align: center;
            margin-bottom: 30px;
            font-size: 2.5em;
        }
        .role-selector {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-bottom: 30px;
        }
        .role-btn {
            padding: 15px 40px;
            font-size: 1.1em;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
            font-weight: bold;
        }
        .admin-btn {
            background: #667eea;
            color: white;
        }
        .admin-btn:hover {
            background: #5568d3;
            transform: translateY(-2px);
        }
        .user-btn {
            background: #764ba2;
            color: white;
        }
        .user-btn:hover {
            background: #653a8a;
            transform: translateY(-2px);
        }
        .section {
            display: none;
            animation: fadeIn 0.5s;
        }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 600;
        }
        input, select {
            width: 100%;
            padding: 12px;
            border: 2px solid #e0e0e0;
            border-radius: 6px;
            font-size: 1em;
            transition: border-color 0.3s;
        }
        input:focus, select:focus {
            outline: none;
            border-color: #667eea;
        }
        button[type="submit"], .action-btn {
            width: 100%;
            padding: 15px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 1.1em;
            font-weight: bold;
            cursor: pointer;
            transition: background 0.3s;
            margin-bottom: 10px;
        }
        button[type="submit"]:hover, .action-btn:hover {
            background: #5568d3;
        }
        .compare-btn {
            background: #28a745;
        }
        .compare-btn:hover {
            background: #218838;
        }
        .result {
            margin-top: 20px;
            padding: 20px;
            border-radius: 8px;
            display: none;
        }
        .result.success {
            background: #d4edda;
            border: 2px solid #c3e6cb;
            color: #155724;
        }
        .result.error {
            background: #f8d7da;
            border: 2px solid #f5c6cb;
            color: #721c24;
        }
        .back-btn {
            margin-top: 20px;
            padding: 10px 20px;
            background: #6c757d;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
        }
        .back-btn:hover {
            background: #5a6268;
        }
        .grid-2 {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
        }
        .soap-link {
            text-align: center;
            margin-top: 20px;
        }
        .soap-link a {
            display: inline-block;
            padding: 12px 30px;
            background: #17a2b8;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-weight: bold;
        }
        .soap-link a:hover {
            background: #138496;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Employee Performance Analytics</h1>
        
        <div id="home" class="section" style="display: block;">
            <div class="role-selector">
                <button class="role-btn admin-btn" onclick="showSection('admin')">Admin Dashboard</button>
                <button class="role-btn user-btn" onclick="showSection('user')">User Prediction</button>
            </div>
            <div class="soap-link">
                <a href="soap-test.jsp">View SOAP Web Services</a>
            </div>
        </div>
        
        <div id="admin" class="section">
            <h2 style="color: #667eea; margin-bottom: 20px;">Admin Dashboard</h2>
            
            <button onclick="compareModels()" class="action-btn compare-btn">Compare Algorithms</button>
            
            <div class="form-group">
                <label for="algorithm">Select Algorithm to Train:</label>
                <select id="algorithm">
                    <option value="J48">J48 Decision Tree</option>
                    <option value="NaiveBayes">Naive Bayes</option>
                    <option value="RandomForest">Random Forest</option>
                </select>
            </div>
            
            <button onclick="trainModel()" class="action-btn">Train Model</button>
            
            <div id="adminResult" class="result"></div>
            <button class="back-btn" onclick="showSection('home')">Back to Home</button>
        </div>
        
        <div id="user" class="section">
            <h2 style="color: #764ba2; margin-bottom: 20px;">Performance Prediction</h2>
            
            <form id="predictionForm">
                <div class="form-group">
                    <label for="employeeId">Employee ID:</label>
                    <input type="text" id="employeeId" name="employeeId" required placeholder="e.g., EMP001">
                </div>
                
                <div class="grid-2">
                    <div class="form-group">
                        <label for="satisfactionLevel">Satisfaction Level (0-1):</label>
                        <input type="number" id="satisfactionLevel" name="satisfactionLevel" min="0" max="1" step="0.01" required placeholder="0.75">
                    </div>
                    
                    <div class="form-group">
                        <label for="lastEvaluation">Last Evaluation (0-1):</label>
                        <input type="number" id="lastEvaluation" name="lastEvaluation" min="0" max="1" step="0.01" required placeholder="0.85">
                    </div>
                </div>
                
                <div class="grid-2">
                    <div class="form-group">
                        <label for="numberProject">Number of Projects:</label>
                        <input type="number" id="numberProject" name="numberProject" min="0" max="10" required placeholder="5">
                    </div>
                    
                    <div class="form-group">
                        <label for="averageMonthlyHours">Average Monthly Hours:</label>
                        <input type="number" id="averageMonthlyHours" name="averageMonthlyHours" min="80" max="320" required placeholder="200">
                    </div>
                </div>
                
                <div class="grid-2">
                    <div class="form-group">
                        <label for="timeSpendCompany">Years at Company:</label>
                        <input type="number" id="timeSpendCompany" name="timeSpendCompany" min="0" max="20" required placeholder="3">
                    </div>
                    
                    <div class="form-group">
                        <label for="department">Department:</label>
                        <select id="department" name="department" required>
                            <option value="sales">Sales</option>
                            <option value="technical">Technical</option>
                            <option value="support">Support</option>
                            <option value="IT">IT</option>
                            <option value="product_mng">Product Management</option>
                            <option value="marketing">Marketing</option>
                            <option value="RandD">R&D</option>
                            <option value="accounting">Accounting</option>
                            <option value="hr">HR</option>
                            <option value="management">Management</option>
                        </select>
                    </div>
                </div>
                
                <div class="grid-2">
                    <div class="form-group">
                        <label for="salary">Salary Level:</label>
                        <select id="salary" name="salary" required>
                            <option value="low">Low</option>
                            <option value="medium">Medium</option>
                            <option value="high">High</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="workAccident">Work Accident:</label>
                        <select id="workAccident" name="workAccident" required>
                            <option value="false">No</option>
                            <option value="true">Yes</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="promotionLast5Years">Promotion in Last 5 Years:</label>
                    <select id="promotionLast5Years" name="promotionLast5Years" required>
                        <option value="false">No</option>
                        <option value="true">Yes</option>
                    </select>
                </div>
                
                <button type="submit">Predict Performance</button>
            </form>
            
            <div id="userResult" class="result"></div>
            <button class="back-btn" onclick="showSection('home')">Back to Home</button>
        </div>
    </div>
    
    <script>
        function showSection(section) {
            var sections = document.querySelectorAll('.section');
            for (var i = 0; i < sections.length; i++) {
                sections[i].style.display = 'none';
            }
            document.getElementById(section).style.display = 'block';
        }
        
        function compareModels() {
            fetch('/performance-analytics/api/admin/compare')
                .then(function(response) { return response.json(); })
                .then(function(data) {
                    var html = '<h3>Algorithm Comparison Results:</h3><ul>';
                    for (var algo in data) {
                        html += '<li><strong>' + algo + ':</strong> ' + data[algo].toFixed(2) + '% accuracy</li>';
                    }
                    html += '</ul>';
                    showResult('adminResult', html, 'success');
                })
                .catch(function(error) {
                    showResult('adminResult', 'Error: ' + error, 'error');
                });
        }
        
        function trainModel() {
            var algorithm = document.getElementById('algorithm').value;
            fetch('/performance-analytics/api/admin/train?algorithm=' + algorithm, { method: 'POST' })
                .then(function(response) { return response.json(); })
                .then(function(data) {
                    showResult('adminResult', '<h3>' + data.message + '</h3>', 'success');
                })
                .catch(function(error) {
                    showResult('adminResult', 'Error: ' + error, 'error');
                });
        }
        
        document.getElementById('predictionForm').addEventListener('submit', function(e) {
            e.preventDefault();
            var formData = new FormData(this);
            fetch('/performance-analytics/api/predict', {
                method: 'POST',
                body: new URLSearchParams(formData)
            })
                .then(function(response) { return response.json(); })
                .then(function(data) {
                    showResult('userResult', '<h3>Predicted Performance: ' + data.prediction + '</h3><p>Confidence: ' + data.confidence.toFixed(2) + '%</p>', 'success');
                })
                .catch(function(error) {
                    showResult('userResult', 'Error: ' + error, 'error');
                });
        });
        
        function showResult(elementId, message, type) {
            var resultDiv = document.getElementById(elementId);
            resultDiv.innerHTML = message;
            resultDiv.className = 'result ' + type;
            resultDiv.style.display = 'block';
        }
    </script>
</body>
</html>
