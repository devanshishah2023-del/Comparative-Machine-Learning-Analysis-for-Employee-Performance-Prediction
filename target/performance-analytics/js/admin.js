// Admin Dashboard JavaScript

let comparisonChart = null;
let confusionMatrixChart = null;

// Compare Algorithms
function compareAlgorithms() {
    const resultsDiv = document.getElementById('comparisonResults');
    resultsDiv.innerHTML = '<div class="loading">Comparing algorithms... This may take a few moments.</div>';
    
    fetch('/performance-analytics/api/admin/compare', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        console.log('Response status:', response.status);
        console.log('Response OK:', response.ok);
        
        // Get response as text first
        return response.text();
    })
    .then(text => {
        console.log('Raw response text:', text);
        
        // Parse JSON
        const data = JSON.parse(text);
        console.log('Parsed data:', data);
        
        // Check for error
        if (data.error) {
            throw new Error(data.error);
        }
        
        displayComparisonResults(data);
    })
    .catch(error => {
        console.error('Full error:', error);
        resultsDiv.innerHTML = `
            <div class="error">
                <h3>Error Comparing Algorithms</h3>
                <p><strong>Error:</strong> ${error.message}</p>
                <p class="hint">Check browser console (F12) for details</p>
            </div>
        `;
    });
}

function displayComparisonResults(data) {
    const resultsDiv = document.getElementById('comparisonResults');
    
    let html = `
        <div class="comparison-header">
            <h3>Algorithm Comparison Results</h3>
            <p>Dataset: ${data.totalInstances} employees (sampled: ${data.sampledInstances})</p>
            <p>Attributes: ${data.totalAttributes}</p>
            <p>Predicting: ${data.classAttribute} (Low/Medium/High)</p>
        </div>
        
        <div class="metrics-table">
            <table>
                <thead>
                    <tr>
                        <th>Algorithm</th>
                        <th>Accuracy (%)</th>
                        <th>Precision (%)</th>
                        <th>Recall (%)</th>
                        <th>F1-Score (%)</th>
                        <th>Kappa</th>
                        <th>MAE</th>
                        <th>RMSE</th>
                    </tr>
                </thead>
                <tbody>
    `;
    
    data.algorithms.forEach(algo => {
        html += `
            <tr>
                <td><strong>${algo.algorithm}</strong></td>
                <td>${algo.accuracy}</td>
                <td>${algo.precision}</td>
                <td>${algo.recall}</td>
                <td>${algo.fMeasure}</td>
                <td>${algo.kappa}</td>
                <td>${algo.meanAbsoluteError}</td>
                <td>${algo.rootMeanSquaredError}</td>
            </tr>
        `;
    });
    
    html += `
                </tbody>
            </table>
        </div>
    `;
    
    resultsDiv.innerHTML = html;
    
    // Create comparison chart
    createComparisonChart(data.algorithms);
}

function createComparisonChart(algorithms) {
    const ctx = document.getElementById('comparisonChart');
    if (!ctx) return;
    
    if (comparisonChart) {
        comparisonChart.destroy();
    }
    
    const labels = algorithms.map(a => a.algorithm);
    const accuracies = algorithms.map(a => a.accuracy);
    const precisions = algorithms.map(a => a.precision);
    const recalls = algorithms.map(a => a.recall);
    const fMeasures = algorithms.map(a => a.fMeasure);
    
    comparisonChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Accuracy',
                    data: accuracies,
                    backgroundColor: 'rgba(54, 162, 235, 0.7)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1
                },
                {
                    label: 'Precision',
                    data: precisions,
                    backgroundColor: 'rgba(75, 192, 192, 0.7)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 1
                },
                {
                    label: 'Recall',
                    data: recalls,
                    backgroundColor: 'rgba(255, 206, 86, 0.7)',
                    borderColor: 'rgba(255, 206, 86, 1)',
                    borderWidth: 1
                },
                {
                    label: 'F1-Score',
                    data: fMeasures,
                    backgroundColor: 'rgba(153, 102, 255, 0.7)',
                    borderColor: 'rgba(153, 102, 255, 1)',
                    borderWidth: 1
                }
            ]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    title: {
                        display: true,
                        text: 'Percentage (%)'
                    }
                }
            },
            plugins: {
                title: {
                    display: true,
                    text: 'Algorithm Performance Comparison'
                },
                legend: {
                    display: true,
                    position: 'top'
                }
            }
        }
    });
}

// Train Model
function trainModel() {
    const algorithm = document.getElementById('algorithmSelect').value;
    const resultsDiv = document.getElementById('trainResults');
    
    if (!algorithm) {
        alert('Please select an algorithm');
        return;
    }
    
    resultsDiv.innerHTML = '<div class="loading">Training model... This may take a few moments.</div>';
    
    fetch('/performance-analytics/api/admin/train', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ algorithm: algorithm })
    })
    .then(response => {
        console.log('Train response status:', response.status);
        return response.text();
    })
    .then(text => {
        console.log('Train raw response:', text);
        const data = JSON.parse(text);
        
        if (data.error) {
            throw new Error(data.error);
        }
        displayTrainingResults(data);
    })
    .catch(error => {
        console.error('Train error:', error);
        resultsDiv.innerHTML = `
            <div class="error">
                <h3>Error Training Model</h3>
                <p><strong>Error:</strong> ${error.message}</p>
            </div>
        `;
    });
}

function displayTrainingResults(data) {
    const resultsDiv = document.getElementById('trainResults');
    
    let html = `
        <div class="training-header">
            <h3>Training Results: ${data.algorithm}</h3>
            <p>Dataset: ${data.totalInstances} instances (sampled: ${data.sampledInstances})</p>
            <p>Attributes: ${data.totalAttributes}</p>
            <p>Trained: ${data.timestamp}</p>
        </div>
        
        <div class="metrics-grid">
            <div class="metric-card">
                <h4>Accuracy</h4>
                <p class="metric-value">${data.metrics.accuracy}%</p>
            </div>
            <div class="metric-card">
                <h4>Precision</h4>
                <p class="metric-value">${data.metrics.precision}%</p>
            </div>
            <div class="metric-card">
                <h4>Recall</h4>
                <p class="metric-value">${data.metrics.recall}%</p>
            </div>
            <div class="metric-card">
                <h4>F1-Score</h4>
                <p class="metric-value">${data.metrics.fMeasure}%</p>
            </div>
            <div class="metric-card">
                <h4>Kappa Statistic</h4>
                <p class="metric-value">${data.metrics.kappa}</p>
            </div>
            <div class="metric-card">
                <h4>MAE</h4>
                <p class="metric-value">${data.metrics.meanAbsoluteError}</p>
            </div>
            <div class="metric-card">
                <h4>RMSE</h4>
                <p class="metric-value">${data.metrics.rootMeanSquaredError}</p>
            </div>
        </div>
    `;
    
    resultsDiv.innerHTML = html;
}

// Get Dataset Statistics
function getDatasetStats() {
    fetch('/performance-analytics/api/admin/dataset/stats')
    .then(response => response.text())
    .then(text => {
        const data = JSON.parse(text);
        console.log('Dataset Statistics:', data);
    })
    .catch(error => {
        console.error('Error fetching dataset stats:', error);
    });
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    getDatasetStats();
    console.log('Admin dashboard loaded');
});
