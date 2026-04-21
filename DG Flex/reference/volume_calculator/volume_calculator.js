// --- Workout Volume Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const addBtn = document.getElementById('add-exercise-btn');
    const calculateBtn = document.getElementById('calculate-volume-btn');
    const exerciseListDiv = document.getElementById('exercise-list');
    let exerciseCount = 0;

    // Function to add a new exercise row
    function addExerciseRow() {
        exerciseCount++;
        const row = document.createElement('div');
        row.classList.add('exercise-row');
        row.setAttribute('data-id', exerciseCount);
        row.innerHTML = `
            <div>
                <label>เซ็ต</label>
                <input type="number" class="sets-input" placeholder="3">
            </div>
            <div>
                <label>ครั้ง</label>
                <input type="number" class="reps-input" placeholder="10">
            </div>
            <div>
                <label>น้ำหนัก</label>
                <input type="number" class="weight-input" placeholder="50">
            </div>
            <button type="button" class="remove-btn" title="ลบท่านี้">×</button>
        `;
        exerciseListDiv.appendChild(row);
    }

    // Add the first row automatically
    addExerciseRow();

    // Event listener for adding new rows
    addBtn.addEventListener('click', addExerciseRow);

    // Event listener for removing rows (using event delegation)
    exerciseListDiv.addEventListener('click', (e) => {
        if (e.target && e.target.classList.contains('remove-btn')) {
            e.target.closest('.exercise-row').remove();
        }
    });

    // Main function to calculate total volume
    function calculateTotalVolume() {
        const resultDiv = document.getElementById('volume-result');
        const unit = document.getElementById('volume-unit').value;
        const exerciseRows = exerciseListDiv.querySelectorAll('.exercise-row');
        let totalVolume = 0;
        let isValid = true;

        if (exerciseRows.length === 0) {
            resultDiv.innerHTML = '<p class="error">กรุณาเพิ่มท่าออกกำลังกายอย่างน้อย 1 ท่า</p>';
            return;
        }

        exerciseRows.forEach(row => {
            const sets = parseFloat(row.querySelector('.sets-input').value);
            const reps = parseFloat(row.querySelector('.reps-input').value);
            const weight = parseFloat(row.querySelector('.weight-input').value);

            if (isNaN(sets) || isNaN(reps) || isNaN(weight) || sets <= 0 || reps <= 0 || weight < 0) {
                isValid = false;
            } else {
                totalVolume += sets * reps * weight;
            }
        });

        if (!isValid) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกข้อมูลในทุกช่องให้ถูกต้อง (ตัวเลขมากกว่า 0)</p>';
            return;
        }
        
        resultDiv.innerHTML = `
            <div class="bmi-result-container" style="margin-top: 30px;">
                <div class="bmi-result-display">
                    <p>Total Workout Volume:</p>
                    <p class="bmi-value normal">${totalVolume.toLocaleString()}</p>
                    <p class="bmi-category normal">${unit}</p>
                </div>
            </div>
        `;
    }

    calculateBtn.addEventListener('click', calculateTotalVolume);
});