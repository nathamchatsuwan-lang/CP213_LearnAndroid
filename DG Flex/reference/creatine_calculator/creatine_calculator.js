// --- Creatine Dosage Calculator ---

document.addEventListener('DOMContentLoaded', () => {
    const calculateBtn = document.getElementById('calculate-creatine-btn');

    function calculateCreatine() {
        const resultDiv = document.getElementById('creatine-result');
        const weightInput = parseFloat(document.getElementById('creatine-weight').value);
        const unit = document.getElementById('weight-units').value;

        // --- Validation ---
        if (isNaN(weightInput) || weightInput <= 0) {
            resultDiv.innerHTML = '<p class="error">กรุณากรอกน้ำหนักให้ถูกต้อง</p>';
            return;
        }

        // --- Unit Conversion to KG ---
        const weightInKg = (unit === 'lbs') ? weightInput / 2.20462 : weightInput;

        // --- Calculations ---
        const loadingDose = weightInKg * 0.3;
        const maintenanceDose = weightInKg * 0.03;

        // --- Display result ---
        resultDiv.innerHTML = `
            <div class="result-table" style="margin-top: 30px;">
                <div style="padding: 15px; background-color: #f9f9f9; border-radius: 8px 8px 0 0;">
                    <h3 style="margin:0; text-align:center;">คำแนะนำสำหรับน้ำหนัก ${weightInKg.toFixed(1)} กก.</h3>
                </div>
                <div style="border: 1px solid #e0e0e0; padding: 20px; border-top: none;">
                    <h4>ระยะโหลด (Loading Phase)</h4>
                    <p class="sub-text">สำหรับ 5-7 วันแรก เพื่อเพิ่มระดับครีเอทีนอย่างรวดเร็ว</p>
                    <p style="font-size: 1.8em; font-weight: bold; color: #E63946; text-align: center; margin: 10px 0;">${loadingDose.toFixed(1)} กรัม/วัน</p>
                    <p class="sub-text" style="text-align: center;">(สามารถแบ่งทานครั้งละ 5 กรัม 3-4 ครั้งต่อวัน)</p>
                </div>

                <div style="border: 1px solid #e0e0e0; padding: 20px; border-top: none; margin-top: -1px;">
                    <h4>ระยะรักษาระดับ (Maintenance Phase)</h4>
                    <p class="sub-text">สำหรับทานต่อเนื่องทุกวันหลังจากจบระยะโหลด</p>
                    <p style="font-size: 1.8em; font-weight: bold; color: #1890ff; text-align: center; margin: 10px 0;">3 - 5 กรัม/วัน</p>
                    <p class="sub-text" style="text-align: center;">(หรือประมาณ ${maintenanceDose.toFixed(1)} กรัม/วัน ตามน้ำหนักตัว)</p>
                </div>

                <div style="border: 1px solid #e0e0e0; padding: 20px; border-top: none; border-radius: 0 0 8px 8px; margin-top: -1px; background-color: #f0faff;">
                    <h4>คำแนะนำการดื่มน้ำ</h4>
                    <p class="sub-text" style="text-align: center; line-height: 1.5;">เนื่องจากครีเอทีนจะดึงน้ำเข้าสู่กล้ามเนื้อ<br>ควรดื่มน้ำให้มากขึ้นกว่าปกติ โดยตั้งเป้าหมายให้ได้<br><strong style="font-size: 1.1em; color: #0056b3;">อย่างน้อย 3-4 ลิตรต่อวัน</strong></p>
                </div>
            </div>
            <p style="text-align: center; font-size: 0.85em; color: #666; margin-top: 15px;">*การทำ Loading Phase ไม่ใช่สิ่งจำเป็น แต่จะช่วยให้เห็นผลเร็วขึ้น*</p>
        `;
    }

    if (calculateBtn) {
        calculateBtn.addEventListener('click', calculateCreatine);
    }
});