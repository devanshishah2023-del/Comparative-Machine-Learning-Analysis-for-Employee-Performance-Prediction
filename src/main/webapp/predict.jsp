<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Predict Employee Performance</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .predict-container {
            max-width: 1200px;
            margin: 50px auto;
            padding: 30px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 15px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.3);
        }
        .predict-form {
            background: white;
            padding: 40px;
            border-radius: 10px;
        }
        .form-title {
            color: #667eea;
            font-size: 2em;
            margin-bottom: 30px;
            text-align: center;
        }
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }
        .form-group {
            display: flex;
            flex-direction: column;
        }
        .form-group label {
            font-weight: bold;
            margin-bottom: 8px;
            color: #333;
        }
        .form-group input, .form-group select {
            padding: 12px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        .form-group input:focus, .form-group select:focus {
            outline: none;
            border-color: #667eea;
        }
        .predict-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 15px 40px;
            font-size: 18px;
            border-radius: 8px;
            cursor: pointer;
            width: 100%;
            margin-top: 20px;
            transition: transform 0.2s;
        }
        .predict-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
        }
        .result-box {
            margin-top: 30px;
            padding: 20px;
            background: #f8f9fa;
            border-radius: 8px;
            display: none;
        }
        .result-box.show {
            display: block;
        }
        .prediction-result {
            font-size: 24px;
            font-weight: bold;
            text-align: center;
            margin: 20px 0;
        }
        .confidence {
            text-align: center;
            font-size: 18px;
            color: #666;
        }
        .badge {
            display: inline-block;
            padding: 10px 20px;
            border-radius: 20px;
            color: white;
        }
        .badge.High { background: #28a745; }
        .badge.Medium { background: #ffc107; color: #333; }
        .badge.Low { background: #dc3545; }
    </style>
</head>
<body>
    <div class="predict-container">
        <div class="predict-form">
            <h1 class="form-title">Predict Employee Performance</h1>
            
            <form id="predictionForm">
                <div class="form-row">
                    <div class="form-group">
                        <label>Employee ID:</label>
                        <input type="number" id="employeeId" name="employeeId" required placeholder="e.g., 12345">
                    </div>
                    
                    <div class="form-group">
                        <label>Department:</label>
                        <select id="department" name="department" required>
                            <option value="">Select Department</option>
                            <option value="Analytics">Analytics</option>
                            <option value="Finance">Finance</option>
                            <option value="HR">HR</option>
                            <option value="Legal">Legal</option>
                            <option value="Operations">Operations</option>
                            <option value="Procurement">Procurement</option>
                            <option value="R&D">R&D</option>
                            <option value="Sales & Marketing">Sales & Marketing</option>
                            <option value="Technology">Technology</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Region:</label>
                        <select id="region" name="region" required>
                            <option value="">Select Region</option>
                            <option value="region_1">Region 1</option>
                            <option value="region_2">Region 2</option>
                            <option value="region_3">Region 3</option>
                            <option value="region_4">Region 4</option>
                            <option value="region_5">Region 5</option>
                            <option value="region_7">Region 7</option>
                            <option value="region_9">Region 9</option>
                            <option value="region_13">Region 13</option>
                            <option value="region_15">Region 15</option>
                            <option value="region_22">Region 22</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label>Education:</label>
                        <select id="education" name="education" required>
                            <option value="">Select Education</option>
                            <option value="Below Secondary">Below Secondary</option>
                            <option value="Bachelors">Bachelor's</option>
                            <option value="Masters & above">Master's & Above</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Gender:</label>
                        <select id="gender" name="gender" required>
                            <option value="">Select Gender</option>
                            <option value="m">Male</option>
                            <option value="f">Female</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label>Recruitment Channel:</label>
                        <select id="recruitmentChannel" name="recruitmentChannel" required>
                            <option value="">Select Channel</option>
                            <option value="sourcing">Sourcing</option>
                            <option value="other">Other</option>
                            <option value="referred">Referred</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Number of Trainings:</label>
                        <input type="number" id="noOfTrainings" name="noOfTrainings" min="1" max="10" required placeholder="1-10">
                    </div>
                    
                    <div class="form-group">
                        <label>Age:</label>
                        <input type="number" id="age" name="age" min="20" max="60" required placeholder="20-60">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Previous Year Rating:</label>
                        <select id="previousYearRating" name="previousYearRating" required>
                            <option value="">Select Rating</option>
                            <option value="1">1 - Poor</option>
                            <option value="2">2 - Below Average</option>
                            <option value="3">3 - Average</option>
                            <option value="4">4 - Good</option>
                            <option value="5">5 - Excellent</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label>Length of Service (years):</label>
                        <input type="number" id="lengthOfService" name="lengthOfService" min="1" max="37" required placeholder="1-37">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>KPIs Met >80%:</label>
                        <select id="kpisMet" name="kpisMet" required>
                            <option value="">Select</option>
                            <option value="1">Yes</option>
                            <option value="0">No</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label>Awards Won:</label>
                        <select id="awardsWon" name="awardsWon" required>
                            <option value="">Select</option>
                            <option value="1">Yes</option>
                            <option value="0">No</option>
                        </select>
                    </div>
                </div>

                <button type="submit" class="predict-btn">Predict Performance</button>
            </form>

            <div id="resultBox" class="result-box">
                <div class="prediction-result">
                    Predicted Performance: <span id="prediction" class="badge">-</span>
                </div>
                <div class="confidence">
                    Confidence: <span id="confidence">0</span>%
                </div>
            </div>
        </div>
    </div>

    <script src="js/prediction.js"></script>
</body>
</html>
