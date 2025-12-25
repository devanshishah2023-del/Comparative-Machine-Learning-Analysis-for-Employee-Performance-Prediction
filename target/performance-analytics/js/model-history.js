// Model History JavaScript

function loadModelHistory() {
    const historyDiv = document.getElementById('modelHistory');
    historyDiv.innerHTML = '<div class="loading">Loading training history...</div>';
    
    fetch('/performance-analytics/api/models')
        .then(response => response.text())
        .then(text => {
            console.log('Model history response:', text);
            const data = JSON.parse(text);
            displayModelHistory(data);
        })
        .catch(error => {
            console.error('Error loading model history:', error);
            historyDiv.innerHTML = `
                <div class="info">
                    <h3>No Training History Yet</h3>
                    <p>Train a model from the Admin page to see history here.</p>
                </div>
            `;
        });
}

function displayModelHistory(data) {
    const historyDiv = document.getElementById('modelHistory');
    
    if (!data.models || data.models.length === 0) {
        historyDiv.innerHTML = `
            <div class="info">
                <h3>No Training History</h3>
                <p>No models have been trained yet. Go to the Admin page to train your first model!</p>
                <a href="admin.jsp" class="btn btn-primary">Go to Admin Page</a>
            </div>
        `;
        return;
    }
    
    let html = '<table><thead><tr>';
    html += '<th>Algorithm</th><th>Accuracy</th><th>Date</th><th>Trained By</th>';
    html += '</tr></thead><tbody>';
    
    data.models.forEach(model => {
        html += '<tr>';
        html += `<td>${model.algorithm}</td>`;
        html += `<td>${model.accuracy}%</td>`;
        html += `<td>${model.trainingDate}</td>`;
        html += `<td>${model.trainedBy}</td>`;
        html += '</tr>';
    });
    
    html += '</tbody></table>';
    historyDiv.innerHTML = html;
}

// Load on page load
document.addEventListener('DOMContentLoaded', function() {
    loadModelHistory();
});
