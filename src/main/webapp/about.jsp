<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>About Us - Employee Performance Analytics</title>
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
        }
        .back-btn {
            padding: 10px 20px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-weight: bold;
        }
        .back-btn:hover {
            background: #5a6268;
        }
        .container {
            background: white;
            padding: 40px;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.2);
            max-width: 1000px;
            margin: 0 auto;
        }
        h2 {
            color: #667eea;
            margin-top: 30px;
            margin-bottom: 15px;
            font-size: 1.8em;
        }
        h3 {
            color: #764ba2;
            margin-top: 20px;
            margin-bottom: 10px;
            font-size: 1.3em;
        }
        p {
            line-height: 1.8;
            margin-bottom: 15px;
            color: #333;
        }
        .team-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }
        .team-member {
            background: #f8f9fa;
            padding: 25px;
            border-radius: 10px;
            text-align: center;
            border: 2px solid #667eea;
        }
        .team-member h4 {
            color: #667eea;
            margin-bottom: 5px;
            font-size: 1.2em;
        }
        .team-member p {
            color: #666;
            font-size: 0.95em;
            margin: 0;
        }
        .features-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin: 20px 0;
        }
        .feature-box {
            background: #e7f3ff;
            padding: 20px;
            border-radius: 8px;
            border-left: 4px solid #667eea;
        }
        .feature-box h4 {
            color: #667eea;
            margin-bottom: 8px;
        }
        .feature-box p {
            font-size: 0.9em;
            margin: 0;
            color: #555;
        }
        .tech-stack {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin: 20px 0;
        }
        .tech-badge {
            background: #667eea;
            color: white;
            padding: 8px 15px;
            border-radius: 20px;
            font-size: 0.9em;
            font-weight: 600;
        }
        .architecture-section {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
        }
        .architecture-section h4 {
            color: #667eea;
            margin-bottom: 10px;
        }
        .architecture-section ul {
            margin-left: 20px;
            line-height: 2;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>About Us</h1>
        <a href="dashboard.jsp" class="back-btn">Back to Dashboard</a>
    </div>
    
    <div class="container">
        <h2>Employee Performance Analytics and Prediction System</h2>
        
        <p>
            Managing employee performance is incredibly important to the success and productivity 
            of an organization. Businesses lose millions of dollars each year from lost productivity 
            and poor employee management caused by a failure to identify performance issues and a 
            lack of meaningful information.
        </p>
        
        <p>
            This project creates a machine learning application to predict ratings of employee 
            performance using real data from Kaggle's HR Employee Performance dataset. The main 
            objectives are predicting employee performance ratings (Low, Medium, High, and Outstanding) 
            and evaluating multiple machine learning algorithms on this public dataset.
        </p>
        
        <h3>Team Members</h3>
        <div class="team-grid">
            <div class="team-member">
                <h4>Devanshi Shah</h4>
                <p>Student ID: 245847050</p>
            </div>
            <div class="team-member">
                <h4>Heta Shukla</h4>
                <p>Student ID: 245820350</p>
            </div>
        </div>
        
        <h3>Key Features</h3>
        <div class="features-grid">
            <div class="feature-box">
                <h4>ML Algorithms</h4>
                <p>Decision Tree, Naive Bayes, and Random Forest comparison</p>
            </div>
            <div class="feature-box">
                <h4>Role-Based Access</h4>
                <p>Admin and User roles with different permissions</p>
            </div>
            <div class="feature-box">
                <h4>Model Management</h4>
                <p>Train, save, and view model history</p>
            </div>
            <div class="feature-box">
                <h4>Predictions</h4>
                <p>Real-time performance predictions with confidence scores</p>
            </div>
            <div class="feature-box">
                <h4>Web Services</h4>
                <p>RESTful and SOAP APIs for integration</p>
            </div>
            <div class="feature-box">
                <h4>History Tracking</h4>
                <p>View past predictions and model training history</p>
            </div>
        </div>
        
        <h3>Technology Stack</h3>
        <div class="tech-stack">
            <span class="tech-badge">Java EE</span>
            <span class="tech-badge">WildFly 18</span>
            <span class="tech-badge">MySQL</span>
            <span class="tech-badge">Weka ML</span>
            <span class="tech-badge">JAX-RS (REST)</span>
            <span class="tech-badge">JAX-WS (SOAP)</span>
            <span class="tech-badge">JPA/Hibernate</span>
            <span class="tech-badge">Maven</span>
            <span class="tech-badge">JSP</span>
            <span class="tech-badge">HTML5/CSS3</span>
            <span class="tech-badge">JavaScript</span>
        </div>
        
        <h3>5-Tier Enterprise Architecture</h3>
        <div class="architecture-section">
            <h4>Tier 1 - Presentation Layer</h4>
            <ul>
                <li>JSP-based web interface with responsive design</li>
                <li>Admin dashboard and user prediction forms</li>
            </ul>
            
            <h4>Tier 2 - Web Services Layer</h4>
            <ul>
                <li>JAX-RS (RESTful) implementation</li>
                <li>JAX-WS (SOAP) implementation</li>
                <li>JSON and XML data interchange formats</li>
            </ul>
            
            <h4>Tier 3 - Business Logic Layer</h4>
            <ul>
                <li>ModelTrainerBean (Stateless EJB): ML training and persistence</li>
                <li>PredictionServiceBean (Stateless EJB): Prediction management</li>
                <li>AuthenticationBean: User authentication and authorization</li>
            </ul>
            
            <h4>Tier 4 - Data Access Layer</h4>
            <ul>
                <li>JPA with Hibernate provider</li>
                <li>Entity mappings: ModelStorage, Prediction, User</li>
                <li>Container-managed transactions</li>
            </ul>
            
            <h4>Tier 5 - Data Layer</h4>
            <ul>
                <li>MySQL database</li>
                <li>Tables: users, model_storage, predictions, employees</li>
                <li>JDBC connection pooling via WildFly datasource</li>
            </ul>
        </div>
        
        <h3>Dataset Information</h3>
        <p>
            The dataset comes from Kaggle's HR Employee Performance collection with approximately 
            1,200 instances and 18 descriptive features. The classes are reasonably balanced with 
            distributions of approximately 15% Low, 35% Medium, 40% High, and 10% Outstanding performers.
        </p>
        
        <h4>Key Features in Dataset:</h4>
        <ul style="margin-left: 20px; line-height: 2;">
            <li><strong>Work Metrics:</strong> Average monthly hours, number of projects, time at company</li>
            <li><strong>Satisfaction Scores:</strong> Satisfaction level, work-life balance, last evaluation</li>
            <li><strong>Career Development:</strong> Promotion history, training hours, salary level</li>
            <li><strong>Demographics:</strong> Department, age, education level, experience</li>
        </ul>
        
        <h3>References</h3>
        <ul style="margin-left: 20px; line-height: 2;">
            <li>
                Dataset: 
                <a href="https://www.kaggle.com/datasets/sanjanchaudhari/employees-performance-for-hr-analytics" 
                   target="_blank" style="color: #667eea;">
                    Kaggle HR Employee Performance Dataset
                </a>
            </li>
            <li>
                Weka Documentation: 
                <a href="https://www.cs.waikato.ac.nz/ml/weka/" target="_blank" style="color: #667eea;">
                    Weka Machine Learning
                </a>
            </li>
            <li>
                WildFly Documentation: 
                <a href="https://docs.wildfly.org/18/" target="_blank" style="color: #667eea;">
                    WildFly 18 Documentation
                </a>
            </li>
        </ul>
    </div>
</body>
</html>
