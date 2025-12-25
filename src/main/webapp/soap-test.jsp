<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>SOAP Web Service Testing</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 15px;
            padding: 40px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.2);
        }
        h1 {
            color: #667eea;
            margin-bottom: 30px;
            text-align: center;
        }
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            padding: 10px 20px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 6px;
        }
        .back-link:hover {
            background: #5a6268;
        }
        .endpoint-section {
            margin-bottom: 30px;
            padding: 20px;
            background: #f8f9fa;
            border-radius: 8px;
        }
        .endpoint-url {
            font-family: 'Courier New', monospace;
            background: white;
            padding: 15px;
            border-radius: 6px;
            margin: 15px 0;
            word-break: break-all;
            border: 2px solid #667eea;
        }
        .wsdl-link {
            display: inline-block;
            padding: 12px 30px;
            background: #17a2b8;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-weight: bold;
            margin-top: 10px;
        }
        .wsdl-link:hover {
            background: #138496;
        }
        .operations {
            margin-top: 20px;
        }
        .operation {
            background: white;
            padding: 15px;
            margin: 10px 0;
            border-radius: 6px;
            border-left: 4px solid #667eea;
        }
        .operation-name {
            font-weight: bold;
            color: #667eea;
            margin-bottom: 5px;
        }
        .operation-desc {
            color: #666;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
    <div class="container">
        <a href="index.jsp" class="back-link">Back to Main Page</a>
        
        <h1>SOAP Web Service Endpoints</h1>
        
        <div class="endpoint-section">
            <h2 style="color: #667eea; margin-bottom: 15px;">Performance Analytics Web Service</h2>
            <p class="endpoint-url">http://localhost:8080/performance-analytics/PerformanceWebService/PerformanceWebServiceImpl?wsdl</p>
            <a href="http://localhost:8080/performance-analytics/PerformanceWebService/PerformanceWebServiceImpl?wsdl" 
               target="_blank" class="wsdl-link">View WSDL</a>
        </div>
        
        <div class="operations">
            <h3 style="color: #667eea; margin-bottom: 15px;">Available Operations:</h3>
            
            <div class="operation">
                <div class="operation-name">predictPerformance</div>
                <div class="operation-desc">Predicts employee performance based on various attributes</div>
            </div>
            
            <div class="operation">
                <div class="operation-name">trainModel</div>
                <div class="operation-desc">Trains a machine learning model with specified algorithm</div>
            </div>
            
            <div class="operation">
                <div class="operation-name">compareAlgorithms</div>
                <div class="operation-desc">Compares accuracy of different ML algorithms</div>
            </div>
            
            <div class="operation">
                <div class="operation-name">getModelInfo</div>
                <div class="operation-desc">Retrieves information about the current trained model</div>
            </div>
        </div>
    </div>
</body>
</html>
