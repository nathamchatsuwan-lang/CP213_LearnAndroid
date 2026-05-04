##วิธีการตั้งค่า JAVA_HOME ในเครื่อง##

สำหรับ Windows:
เปิดเมนู Start ค้นหาคำว่า "Environment Variables" แล้วเลือก "Edit the system environment variables"
คลิกที่ปุ่ม "Environment Variables..." (ตัวแปรสภาพแวดล้อม) ที่ด้านล่างขวา
ในส่วนของ System variables (ตัวแปรระบบ) ด้านล่าง ให้คลิก "New..." (สร้าง...)
กรอกข้อมูลดังนี้:
Variable name: JAVA_HOME
Variable value: ใส่ Path ที่คุณติดตั้ง JDK เอาไว้ (เช่น C:\Program Files\Java\jdk-17 หรือเวอร์ชันที่คุณใช้งาน)
จากนั้นในตาราง System variables เดิม ให้หาตัวแปรที่ชื่อว่า Path เลือกแล้วคลิก "Edit..."
คลิก "New" แล้วเพิ่ม %JAVA_HOME%\bin ลงไป กด OK ทั้งหมดเพื่อบันทึกการตั้งค่า

สำหรับ macOS:
เปิด Terminal
เปิดไฟล์คอนฟิกของ Shell ที่คุณใช้งาน (เช่น ~/.zshrc หรือ ~/.bash_profile) ด้วย Text Editor เช่น nano ~/.zshrc
เพิ่มโค้ดบรรทัดนี้ลงไป:
export JAVA_HOME=$(/usr/libexec/java_home)
บันทึกไฟล์ และรันคำสั่ง source ~/.zshrc เพื่อให้การตั้งค่ามีผลทันที

Tech Stack
ภาษา: Kotlin
UI Framework: Jetpack Compose ร่วมกับ Material 3
การนำทาง (Navigation): Compose Destinations (การนำทางแบบ Type-safe)
Dependency Injection: Hilt (Dagger)
ฐานข้อมูลในเครื่อง (Local Database): Room
การจัดเก็บข้อมูล (Data Storage): DataStore (Preferences และ Proto)
Serialization: Kotlinx Serialization และ Protocol Buffers
การแสดงภาพข้อมูล (Visualizations): Vico (ไลบรารีสำหรับสร้างกราฟและแผนภูมิประสิทธิภาพสูง)
สถาปัตยกรรม (Architecture): MVVM (Model-View-ViewModel) พร้อมหลักการ Clean Architecture

ฟีเจอร์หลัก
หน้าแดชบอร์ดส่วนตัว (Personalized Home Dashboard): ดูภาพรวมความคืบหน้าปัจจุบันและการออกกำลังกายที่กำลังจะมาถึงได้อย่างรวดเร็ว
การติดตามการออกกำลังกายที่ครอบคลุม (Comprehensive Workout Tracking): บันทึกเซ็ต จำนวนครั้ง (reps) และน้ำหนักแบบเรียลไทม์ระหว่างช่วงที่คุณอยู่ในยิม
คลังท่าออกกำลังกาย (Exercise Library): ดู สร้าง และจัดการฐานข้อมูลท่าออกกำลังกายขนาดใหญ่ พร้อมรองรับการกำหนดท่าออกกำลังกายเอง
ประวัติขั้นสูง (Advanced History): ย้อนดูการออกกำลังกายที่ผ่านมาพร้อมบันทึกรายละเอียดและสรุปผลการฝึก
สถิติและการวิเคราะห์เชิงลึก (Dynamic Statistics & Analytics): แสดงภาพรวมความคืบหน้าของคุณเมื่อเวลาผ่านไปผ่านแผนภูมิแบบอินเทอร์แอกทีฟ
เครื่องคำนวณฟิตเนส (Fitness Calculators): เข้าถึงเครื่องมือที่จำเป็น เช่น เครื่องคำนวณ 1RM (One Rep Max) และอื่นๆ
แผนการออกกำลังกายที่ยืดหยุ่น (Flexible Workout Plans): สร้างและทำตามโปรแกรมที่มีโครงสร้างชัดเจนซึ่งปรับให้เข้ากับเป้าหมายของคุณ
การออกแบบที่ตอบสนอง (Responsive Design): UI ที่สวยงามและลื่นไหล รองรับโหมดมืด (Dark mode) และไมโครแอนิเมชัน

การออกแบบและโครงร่าง (Design & Wireframes)
แอปพลิเคชันนี้ใช้ ดีไซน์โทนสีมืดโดยอิงตามหลักการออกแบบของ Material 3 คุณสามารถดูโครงร่าง (Wireframes) ที่มีความละเอียดสูงซึ่งปรับให้เหมาะสมสำหรับการนำเข้า Figma ได้ในโฟลเดอร์ wireframe_export:
หรือที่ https://www.figma.com/design/jCuXscubh2n7Vub0DZaQup/Untitled?node-id=0-1&t=3huMRHWKvq6xSvTi-1
ดัชนีรวมผลงานการออกแบบ (Design Gallery Index)
หน้าแดชบอร์ดหลัก (Home Dashboard)
โหมดกำลังออกกำลังกาย (Active Workout)
เครื่องคำนวณและเครื่องมือ (Calculators & Tools)

การติดตั้งและการตั้งค่า (Installation & Setup)
โคลนพื้นที่จัดเก็บ (Clone repository): git clone https://github.com/nathamchatsuwan-lang/CP213_LearnAndroid.git
เปิดโปรเจกต์ใน Android Studio
ซิงค์ Gradle (Sync Gradle) และบิลด์โปรเจกต์
รันโมดูล app บนอีมูเลเตอร์หรืออุปกรณ์จริง