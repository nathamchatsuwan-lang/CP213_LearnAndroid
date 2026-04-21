// --- Pace Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calcModeRadios = document.querySelectorAll('input[name="calc-mode"]');
    const distanceGroup = document.getElementById('distance-group');
    const timeGroup = document.getElementById('time-group');
    const paceGroup = document.getElementById('pace-group');
    const calculateBtn = document.getElementById('calculate-pace-btn');
    const resultDiv = document.getElementById('pace-result');

    const MILE_TO_KM = 1.60934;

    function updateFormState() {
        const mode = document.querySelector('input[name="calc-mode"]:checked').value;
        
        [distanceGroup, timeGroup, paceGroup].forEach(g => {
            g.classList.remove('disabled');
            g.querySelectorAll('input, select').forEach(i => i.disabled = false);
        });

        if (mode === 'pace') {
            paceGroup.classList.add('disabled');
            paceGroup.querySelectorAll('input, select').forEach(i => i.disabled = true);
        } else if (mode === 'time') {
            timeGroup.classList.add('disabled');
            timeGroup.querySelectorAll('input, select').forEach(i => i.disabled = true);
        } else if (mode === 'dist') {
            distanceGroup.classList.add('disabled');
            distanceGroup.querySelectorAll('input, select').forEach(i => i.disabled = true);
        }
    }

    function calculate() {
        const mode = document.querySelector('input[name="calc-mode"]:checked').value;
        resultDiv.innerHTML = '';

        try {
            if (mode === 'pace') calculatePace();
            else if (mode === 'time') calculateTime();
            else if (mode === 'dist') calculateDistance();
        } catch (e) {
            resultDiv.innerHTML = `<p class="error">${e.message}</p>`;
        }
    }

    function calculatePace() {
        const dist = parseFloat(document.getElementById('dist-val').value);
        const distUnit = document.getElementById('dist-unit').value;
        const hr = parseFloat(document.getElementById('time-hr').value) || 0;
        const min = parseFloat(document.getElementById('time-min').value) || 0;
        const sec = parseFloat(document.getElementById('time-sec').value) || 0;

        if (isNaN(dist) || dist <= 0) throw new Error("กรุณากรอกระยะทางให้ถูกต้อง");
        const totalTimeSec = (hr * 3600) + (min * 60) + sec;
        if (totalTimeSec <= 0) throw new Error("กรุณากรอกเวลาให้ถูกต้อง");

        const distKm = (distUnit === 'mi') ? dist * MILE_TO_KM : dist;
        const paceUnit = document.getElementById('pace-unit').value;
        const targetDistKm = (paceUnit === 'min/mi') ? MILE_TO_KM : 1;

        const secPerTargetDist = (totalTimeSec / distKm) * targetDistKm;
        
        const paceMin = Math.floor(secPerTargetDist / 60);
        const paceSec = Math.round(secPerTargetDist % 60);

        displayResult(`Pace ที่คำนวณได้:`, `${paceMin}:${paceSec.toString().padStart(2, '0')}`, paceUnit);
    }

    function calculateTime() {
        const dist = parseFloat(document.getElementById('dist-val').value);
        const distUnit = document.getElementById('dist-unit').value;
        const paceMin = parseFloat(document.getElementById('pace-min').value) || 0;
        const paceSec = parseFloat(document.getElementById('pace-sec').value) || 0;
        const paceUnit = document.getElementById('pace-unit').value;
        
        if (isNaN(dist) || dist <= 0) throw new Error("กรุณากรอกระยะทางให้ถูกต้อง");
        const paceTotalSec = (paceMin * 60) + paceSec;
        if (paceTotalSec <= 0) throw new Error("กรุณากรอก Pace ให้ถูกต้อง");
        
        const distKm = (distUnit === 'mi') ? dist * MILE_TO_KM : dist;
        const paceIsPerMi = paceUnit === 'min/mi';

        let totalTimeSec;
        if (paceIsPerMi) {
            totalTimeSec = paceTotalSec * (distKm / MILE_TO_KM);
        } else {
            totalTimeSec = paceTotalSec * distKm;
        }

        const hr = Math.floor(totalTimeSec / 3600);
        totalTimeSec %= 3600;
        const min = Math.floor(totalTimeSec / 60);
        const sec = Math.round(totalTimeSec % 60);

        displayResult(`เวลาที่คำนวณได้:`, `${hr} ชม. ${min} นาที ${sec} วินาที`, '');
    }

    function calculateDistance() {
        const hr = parseFloat(document.getElementById('time-hr').value) || 0;
        const min = parseFloat(document.getElementById('time-min').value) || 0;
        const sec = parseFloat(document.getElementById('time-sec').value) || 0;
        const paceMin = parseFloat(document.getElementById('pace-min').value) || 0;
        const paceSec = parseFloat(document.getElementById('pace-sec').value) || 0;
        const paceUnit = document.getElementById('pace-unit').value;

        const totalTimeSec = (hr * 3600) + (min * 60) + sec;
        if (totalTimeSec <= 0) throw new Error("กรุณากรอกเวลาให้ถูกต้อง");
        const paceTotalSec = (paceMin * 60) + paceSec;
        if (paceTotalSec <= 0) throw new Error("กรุณากรอก Pace ให้ถูกต้อง");
        
        const paceIsPerMi = paceUnit === 'min/mi';
        const distUnit = document.getElementById('dist-unit').value;
        
        let distKm = totalTimeSec / (paceIsPerMi ? paceTotalSec / MILE_TO_KM : paceTotalSec);
        
        let finalDist = (distUnit === 'mi') ? distKm / MILE_TO_KM : distKm;

        displayResult(`ระยะทางที่คำนวณได้:`, `${finalDist.toFixed(2)}`, distUnit);
    }
    
    function displayResult(title, value, unit) {
        resultDiv.innerHTML = `
            <div class="bmi-result-container" style="margin-top: 30px;">
                <div class="bmi-result-display">
                    <p>${title}</p>
                    <p class="bmi-value normal">${value}</p>
                    <p class="bmi-category normal">${unit}</p>
                </div>
            </div>
        `;
    }

    calcModeRadios.forEach(radio => radio.addEventListener('change', updateFormState));
    calculateBtn.addEventListener('click', calculate);
    
    // Initial state
    updateFormState();
});