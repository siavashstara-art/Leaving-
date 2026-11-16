# راهنمای جامع و چک‌لیست انتشار اپلیکیشن «هنر زیستن» (Honar Zistan) در گوگل پلی

این سند شامل تمامی مراحل، پیش‌نیازها و فایل‌های آماده برای انتشار موفق اپلیکیشن اندرویدی «هنر زیستن» در **گوگل پلی کنسول (Google Play Console)** است.

---

## ۱. مشخصات فنی آماده‌سازی برنامه (App Readiness)

- **نام اپلیکیشن:** هنر زیستن (Honar Zistan)
- **شناسه انحصاری (Application ID):** `com.aistudio.honarzistan.mrvkqz`
- **نسخه SDK:** 
  - `compileSdk`: 36
  - `targetSdk`: 36
  - `minSdk`: 24 (پوشش بیش از ۹۸٪ دستگاه‌های فعال اندروید)
- **نسخه برنامه:**
  - `versionCode`: 1
  - `versionName`: "1.0"
- **پیکربندی امضای دیجیتال (Signing Config):**
  - فایل کلید امضای انتشار (`my-upload-key.jks`) در ریشه پروژه تنظیم شده و در فایل `app/build.gradle.kts` برای بیلد نسخه Release پیکربندی شده است.

---

## ۲. ساخت فایل خروجی استاندارد گوگل پلی (Android App Bundle - AAB)

گوگل پلی برای انتشار برنامه‌ها نیازمند فرمت `.aab` (Android App Bundle) است. برای ساخت این فایل در محیط پروژه، دستور زیر را در ترمینال اجرا کنید:

```bash
gradle bundleRelease
```

پس از اتمام بیلد، فایل خروجی `.aab` در مسیر زیر ایجاد می‌شود:
`app/build/outputs/bundle/release/app-release.aab`

این فایل آماده آپلود مستقیم در بخش **Production** یا **Testing** گوگل پلی کنسول است.

---

## ۳. مراحل گام‌به‌گام انتشار در Google Play Console

### گام اول: ایجاد حساب توسعه‌دهنده گوگل پلی
1. به سایت [Google Play Console](https://play.google.com/console) مراجعه کنید.
2. با حساب گوگل خود وارد شده و هزینه ثبت‌نام (یک‌بار پرداخت ۲۵ دلار) را پرداخت کنید.
3. اطلاعات حساب (نام توسعه‌دهنده، ایمیل پشتیبانی و وب‌سایت) را تکمیل کنید.

### گام دوم: ایجاد اپلیکیشن جدید
1. در صفحه اصلی کنسول، روی کلید **Create app** کلیک کنید.
2. **App name:** `Honar Zistan` (یا `هنر زیستن`)
3. **Default language:** فارسی (Persian) یا انگلیسی (US English)
4. **App type:** App (برنامه)
5. **Free or paid:** Free (رایگان) یا Paid (پولی) - مطابق مدل کسب‌وکار انتخابی.
6. تایید قوانین برنامه‌های توسعه‌دهنده و خط‌مشی‌ها و کلیک روی **Create app**.

### گام سوم: تکمیل بخش Store Listing (اطلاعات فروشگاه)
برای جذب مخاطبان در گوگل پلی آمریکا و جهان:
- **App Name (حداکثر ۳۰ کاراکتر):** `Honar Zistan`
- **Short Description (حداکثر ۸۰ کاراکتر):** 
  *راهنمای فلسفه زندگی، خودشناسی و عبور از بحران‌های روحی و فقدان.*
- **Full Description (تا ۴۰۰0 کاراکتر):**
  معرفی کتاب «هنر زیستن» اثر سیاوش علی‌میری، روایت‌های مستند از گذر از تاریکی مطلق (فقدان والدین، بحران‌های زندگی)، آشتی با خود در آینه، و ۷ فصل خودشناسی، امید، مسئولیت‌پذیری و ساختن زندگی معنادار.
- **Graphic Assets (گرافیک‌ها):**
  - **App Icon:** آیکون تطبیقی اختصاصی (۵۱۲ در ۵۱۲ پیکسل، فرمت PNG).
  - **Feature Graphic:** بنر گرافیکی جذاب (۱۰۲۴ در ۵۰۰ پیکسل).
  - **Phone Screenshots:** حداقل ۲ تا ۸ اسکرین‌شات از محیط اپلیکیشن (صفحه اصلی، فصل‌ها، بخش صوتی/متنی).

### گام چهارم: تکمیل پرسشنامه‌ها و تنظیمات اجباری
1. **Content Rating (رده‌بندی سنی):**
   - پرسشنامه محتوا را تکمیل کنید (با توجه به محتوای فلسفی و خودشناسی، معمولاً رده ۳+ یا ۱۲+ تعلق می‌گیرد).
2. **Target Audience and Content (مخاطب هدف):**
   - تعیین سنین هدف (مثلاً ۱۸ سال به بالا یا نوجوانان و بزرگسالان).
3. **Data Safety (امنیت داده‌ها):**
   - اعلام نحوه مدیریت داده‌ها (در صورتی که داده‌ها محلی روی دستگاه ذخیره شوند، اعلام کنید که هیچ داده شخصی به سرورهای شخص ثالث ارسال نمی‌شود).
4. **Privacy Policy (خط‌مشی رازداری):**
   - درج لینک صفحه سیاست حفظ حریم خصوصی (الزامی برای گوگل پلی).

### گام پنجم: آپلود فایل AAB و انتشار
1. به بخش **Production** (یا **Closed testing / Open testing**) بروید.
2. روی **Create new release** کلیک کنید.
3. فایل `app-release.aab` را آپلود کنید.
4. یادداشت انتشار (Release notes) را وارد کنید (مثلا: "Release version 1.0 - Initial release of Honar Zistan app.").
5. روی **Save** و سپس **Review release** کلیک کنید و در نهایت درخواست بررسی و انتشار (**Start rollout to Production**) را تایید کنید.

---

## ۴. نمونه متن Privacy Policy (سیاست حفظ حریم خصوصی)

برای قرار دادن در بخش Privacy Policy گوگل پلی می‌توانید از متن زیر استفاده کنید:

> **Privacy Policy for Honar Zistan**
> Honar Zistan ("we", "our", or "app") is designed to provide self-knowledge, philosophical chapters, and personal growth content. We respect your privacy.
> - **Data Collection:** Honar Zistan does not collect, store, or share any personal user data, location, or sensitive information on external servers. All reading progress and local preferences are stored securely and locally on your device.
> - **Permissions:** The app requests minimal device permissions strictly required for its core functionality.
> - **Contact:** If you have any questions regarding privacy, please contact the developer via the support email.

---
*تمامی مقدمات فنی، بیلد ریلیز، امضای دیجیتال و ساختار پروژه برای انتشار در گوگل پلی فراهم شده است.*
