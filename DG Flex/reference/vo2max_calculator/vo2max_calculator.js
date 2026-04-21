// --- VO2 Max Estimation Calculator (Rockport 1-Mile Walk Test) ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-vo2max-btn');
    
    // VO2 Max norms for men (ml/kg/min)
    const vo2MaxMen = {
        '18-25': { excellent: '>60', good: '52-60', above_avg: '47-51', avg: '42-46', below_avg: '37-41', poor: '30-36', very_poor: '<30' },
        '26-35': { excellent: '>56', good: '49-56', above_avg: '43-48', avg: '40-42', below_avg: '35-39', poor: '30-34', very_poor: '<30' },
        '36-45': { excellent: '>51', good: '45-51', above_avg: '39-44', avg: '35-38', below_avg: '31-34', poor: '26-30', very_poor: '<26' },
        '46-55': { excellent: '>45', good: '41-45', above_avg: '36-40', avg: '32-35', below_avg: '29-31', poor: '25-28', very_poor: '<25' },
        '56-65': { excellent: '>41', good: '38-41', above_avg: '33-37', avg: '30-32', below_avg: '26-29', poor: '22-25', very_poor: '<22' },
        '65+':   { excellent: '>37', good: '34-37', above_avg: '30-33', avg: '28-29', below_avg: '23-27', poor: '20-22', very_poor: '<20' }
    };
    // VO2 Max norms for women (ml/kg/min)
    const vo2MaxWomen = {
        '18-25': { excellent: '>56', good: '47-56', above_avg: '42-46', avg: '38-41', below_avg: '33-37', poor: '28-32', very_poor: '<28' },
        '26-35': { excellent: '>52', good: '45-52', above_avg: '39-44', avg: '35-38', below_avg: '31-34', poor: '26-30', very_poor: '<26' },
        '36-45': { excellent: '>45', good: '39-45', above_avg: '35-38', avg: '32-34', below_avg: '28-31', poor: '24-27', very_poor: '<24' },
        '46-55': { excellent: '>40', good: '36-40', above_avg: '32-35', avg: '29-31', below_avg: '25-28', poor: '22-24', very_poor: '<22' },
        '56-65': { excellent: '>37', good: '33-37', above_avg: '29-32', avg: '26-28', below_avg: '23-25', poor: '20-22', very_poor: '<20' },
        '65+':   { excellent: '>32', good: '29-32', above_avg: '26-28', avg: '24-25', below_avg: '21-23', poor: '18-20', very_poor: '<18' }
    };
    const ratingMap = { excellent: 'ยอดเยี่ยม', good: 'ดีมาก', above_avg: 'ค่อนข้างดี', avg: 'ปานกลาง', below_avg: 'ค่อนข้างต่ำ', poor: 'ต่ำ', very_poor: 'ต่ำมาก' };

    function getFitnessRating(vo2, age, gender) {
        const norms = gender === 'male' ? vo2MaxMen : vo2MaxWomen;
        let ageGroup;
        if (age <= 25) ageGroup = '18-25';
        else if (age <= 35) ageGroup = '26-35';
        else if (age <= 45) ageGroup = '36-45';
        else if (age <= 55) ageGroup = '46-55';
        else if (age <= 65) ageGroup = '56-65';
        else ageGroup = '65+';
        
        const ageNorms = norms[ageGroup];
        for (const rating in ageNorms) {
            const range = ageNorms[rating];
            if (range.startsWith('>')) {
                if (vo2 > parseFloat(range.substring(1))) return ratingMap[rating];
            } else if (range.startsWith('<')) {
                if (vo2 < parseFloat(range.substring(1))) return ratingMap[rating];
            } else {
                const [low, high] = range.split('-').map(Number);
                if (vo2 >= low && vo2 <= high) return ratingMap[rating];
            }
        }
        return 'ไม่สามารถประเมินได้';
    }

    function calculateVO2max() {
        const resultDiv = document.getElementById('vo2max-result');
        const gender = document.querySelector('input[name="gender"]:checked').value;
        const age = parseInt(document.getElementById('vo2max-age').value);
        const weight = parseFloat(document.getElementById('vo2max-weight').value);
        const weightUnit = document.getElementById('weight-units').value;
        const timeMin = parseFloat(document.getElementById('walk-time-min').value);
        const timeSec = parseFloat(document.getElementById('walk-time-sec').value);
        const heartRate = parseInt(document.getElementById('final-hr').value);

        // --- Validation ---
        if (isNaN(gender) || isNaN(age) || isNaN(weight) || isNaN(timeMin) || isNaN(timeSec) || isNaN(heartRate)) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกข้อมูลให้ครบทุกช่อง</p>';
            return;
        }

        // --- Calculations ---
        const weightLbs = (weightUnit === 'kg') ? weight * 2.20462 : weight;
        const timeDecimal = timeMin + (timeSec / 60);
        const genderValue = (gender === 'male') ? 1 : 0;
        
        // Rockport 1-Mile Walk Test Formula
        const vo2max = 132.853 - (0.0769 * weightLbs) - (0.3877 * age) + (6.315 * genderValue) - (3.2649 * timeDecimal) - (0.1565 * heartRate);

        const fitnessRating = getFitnessRating(vo2max, age, gender);
        
        // --- Display result ---
        resultDiv.innerHTML = `
            <div class="bmi-result-container" style="margin-top: 30px;">
                <div class="bmi-result-display">
                    <p>ค่า VO2 Max โดยประมาณของคุณคือ:</p>
                    <p class="bmi-value normal">${vo2max.toFixed(2)}</p>
                    <p class="bmi-category normal">mL/kg/min</p>
                     <hr style="margin: 20px 0; border: none; border-top: 1px solid #eee;">
                    <p>ระดับความฟิตของคุณเมื่อเทียบกับคนเพศและวัยเดียวกันคือ:</p>
                    <p style="font-size: 1.5em; font-weight: bold; color: #1890ff; margin-top: 10px;">${fitnessRating}</p>
                </div>
            </div>
        `;
    }

    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateVO2max);
    }
});