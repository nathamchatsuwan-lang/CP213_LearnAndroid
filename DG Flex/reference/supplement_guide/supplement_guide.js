// --- Supplement Guide ---

document.addEventListener('DOMContentLoaded', () => {
    const supplementSelect = document.getElementById('supplement-select');
    const infoDiv = document.getElementById('supplement-info');

    const supplementData = {
        protein: `
            <h3>โปรตีน (Protein)</h3>
            <p>เป็นสารอาหารหลักที่จำเป็นต่อการสร้างและซ่อมแซมกล้ามเนื้อ การได้รับโปรตีนให้เพียงพอเป็นสิ่งสำคัญที่สุดสำหรับทุกคน</p>
            <h4>ปริมาณที่แนะนำ:</h4>
            <p>ปริมาณโปรตีนที่ต้องการขึ้นอยู่กับน้ำหนักตัวและเป้าหมายของแต่ละบุคคล ซึ่งคุณสามารถคำนวณได้จากเครื่องมือของเราโดยตรง</p>
            <a href="../protein_calculator/protein_calculator.html" class="btn-primary" style="width: auto; padding: 10px 20px; text-decoration: none;">ไปที่เครื่องคำนวณโปรตีน</a>
        `,
        creatine: `
            <h3>ครีเอทีน (Creatine Monohydrate)</h3>
            <p>เป็นหนึ่งในอาหารเสริมที่ถูกวิจัยมากที่สุด ช่วยเพิ่มพละกำลังและความแข็งแรงของกล้ามเนื้อในการออกกำลังกายที่ใช้แรงสูงในระยะสั้น</p>
            <h4>ปริมาณที่แนะนำ:</h4>
            <p>ปริมาณที่เหมาะสมจะขึ้นอยู่กับน้ำหนักตัวและระยะของการใช้งาน (โหลด/รักษาระดับ) เราได้สร้างเครื่องคำนวณโดยละเอียดไว้ให้แล้ว</p>
            <a href="../creatine_calculator/creatine_calculator.html" class="btn-primary" style="width: auto; padding: 10px 20px; text-decoration: none;">ไปที่เครื่องคำนวณครีเอทีน</a>
        `,
        bcaas: `
            <h3>บีซีเอเอ (BCAAs - Branched-Chain Amino Acids)</h3>
            <p>คือกลุ่มของกรดอะมิโนจำเป็น 3 ชนิด (Leucine, Isoleucine, Valine) ซึ่งมีบทบาทในการสังเคราะห์โปรตีนในกล้ามเนื้อ</p>
            <h4>ปริมาณที่แนะนำ:</h4>
            <p>ปริมาณที่ใช้กันทั่วไปคือ <strong>10-20 กรัม</strong> สามารถทานระหว่างออกกำลังกายได้</p>
            <p><strong>ข้อควรรู้:</strong> หากคุณได้รับโปรตีนจากอาหารหลักหรือเวย์โปรตีนเพียงพอต่อวันแล้ว ร่างกายของคุณก็มักจะได้รับ BCAAs ที่เพียงพอเช่นกัน การรับประทานเสริมจึงอาจไม่มีความจำเป็น</p>
        `
    };

    function showInfo() {
        const selectedValue = supplementSelect.value;
        if (supplementData[selectedValue]) {
            infoDiv.innerHTML = `
                <div style="background-color: #fafafa; border: 1px solid #f0f0f0; padding: 25px; border-radius: 8px;">
                    ${supplementData[selectedValue]}
                </div>
            `;
        } else {
            infoDiv.innerHTML = '';
        }
    }

    if (supplementSelect) {
        supplementSelect.addEventListener('change', showInfo);
    }
});