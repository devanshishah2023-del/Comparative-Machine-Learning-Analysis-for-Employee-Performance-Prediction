document.getElementById('predictionForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const data = {};
    formData.forEach((value, key) => {
        // Convert to appropriate types
        if (key === 'employeeId' || key === 'noOfTrainings' || key === 'age' || 
            key === 'previousYearRating' || key === 'lengthOfService' || 
            key === 'kpisMet' || key === 'awardsWon') {
            data[key] = parseInt(value);
        } else {
            data[key] = value;
        }
    });
    
    console.log('Sending prediction request:', data);
    
    try {
        const response = await fetch('/performance-analytics/api/predict', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        
        if (!response.ok) {
            throw new Error('HTTP error! status: ' + response.status);
        }
        
        const result = await response.json();
        console.log('Prediction result:', result);
        
        // Handle response safely
        const prediction = result.prediction || 'Unknown';
        const confidence = result.confidence ? Number(result.confidence).toFixed(2) : '0.00';
        
        document.getElementById('prediction').textContent = prediction;
        document.getElementById('prediction').className = 'badge ' + prediction;
        document.getElementById('confidence').textContent = confidence;
        document.getElementById('resultBox').classList.add('show');
        
    } catch (error) {
        console.error('Error:', error);
        alert('Error making prediction: ' + error.message);
    }
});
