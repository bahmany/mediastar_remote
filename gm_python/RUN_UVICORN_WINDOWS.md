# اجرای FastAPI با Uvicorn داخل Windows (با .venv) + Kill کردن قبل از Restart

این سند برای پروژه `gmscreen/gm_python` نوشته شده و هدفش این است که قبل از اجرای مجدد سرور، فرآیندهای قبلی را درست متوقف کنید و مطمئن شوید پورت `8000` آزاد است.

---

## 1) اجرای سرور با `.venv`

### 1.1 فعال‌سازی محیط مجازی

در PowerShell:

```powershell
# در مسیر پروژه
.\.venv\Scripts\Activate.ps1
```

اگر فعال‌سازی درست باشد، ابتدای خط ترمینال چیزی مثل این می‌بینید:

```
(.venv) PS D:\projects\gmscreen\gm_python>
```

### 1.2 اجرای uvicorn

```powershell
python -m uvicorn app.main:app --reload --host 127.0.0.1 --port 8000
```

نکته‌ها:
- `--reload` برای توسعه است (با هر تغییر فایل، سرور ری‌استارت می‌شود).
- `--host 127.0.0.1` فقط روی سیستم خودتان قابل دسترسی است.
- اگر می‌خواهید از شبکه هم دسترسی داشته باشید:

```powershell
python -m uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

---

## 2) Stop کردن uvicorn (روش ساده)

### 2.1 Stop در همان ترمینال
وقتی uvicorn در حال اجراست، داخل همان پنجره‌ی ترمینال:

- `Ctrl + C`

این روش معمولاً تمیزترین راه است.

---

## 3) Kill کردن فرآیندهای فعال روی پورت 8000 (قبل از Restart)

گاهی uvicorn یا یک پروسه‌ی دیگر روی پورت 8000 باقی می‌ماند و اجرای مجدد با خطای «Address already in use» مواجه می‌شود. مراحل زیر پورت را آزاد می‌کند.

### 3.1 پیدا کردن PIDهای فعال روی پورت 8000 (PowerShell)

```powershell
Get-NetTCPConnection -LocalPort 8000 -State Listen | Select-Object -Property LocalAddress,LocalPort,OwningProcess
```

برای دیدن نام پروسه‌ها:

```powershell
Get-NetTCPConnection -LocalPort 8000 -State Listen |
  Select-Object -ExpandProperty OwningProcess |
  Sort-Object -Unique |
  ForEach-Object { Get-Process -Id $_ }
```

### 3.2 Kill کردن تمام PIDهای LISTEN روی پورت 8000 (PowerShell)

```powershell
Get-NetTCPConnection -LocalPort 8000 -State Listen |
  Select-Object -ExpandProperty OwningProcess |
  Sort-Object -Unique |
  ForEach-Object { Stop-Process -Id $_ -Force }
```

اگر PowerShell اجازه نداد، ترمینال را **Run as Administrator** باز کنید.

---

## 4) روش جایگزین با `netstat` (CMD/PowerShell)

### 4.1 پیدا کردن PID

```powershell
netstat -ano | findstr :8000
```

خروجی مثل:

```
TCP    127.0.0.1:8000    0.0.0.0:0    LISTENING    12345
```

### 4.2 Kill کردن PID

```powershell
taskkill /F /PID 12345
```

### 4.3 Kill کردن همه PIDهای مرتبط با 8000 (یک‌خطی)

```powershell
for /f "tokens=5" %a in ('netstat -ano ^| findstr :8000 ^| findstr LISTENING') do taskkill /F /PID %a
```

نکته:
- اگر این را داخل فایل `.bat` گذاشتید، باید `%a` را به `%%a` تغییر بدهید.

---

## 5) روال پیشنهادی «Stop → آزاد کردن پورت → Start»

روال سریع پیشنهادی (PowerShell):

1) اگر پنجره‌ی uvicorn باز است: `Ctrl + C`
2) سپس برای اطمینان:

```powershell
Get-NetTCPConnection -LocalPort 8000 -State Listen |
  Select-Object -ExpandProperty OwningProcess |
  Sort-Object -Unique |
  ForEach-Object { Stop-Process -Id $_ -Force }
```

3) بعد اجرا:

```powershell
python -m uvicorn app.main:app --reload --host 127.0.0.1 --port 8000
```

---

## 6) نکته درباره چند فرآیند uvicorn (reloader)

وقتی `--reload` فعال است، uvicorn معمولاً **دو پروسه** ایجاد می‌کند:
- یکی **reloader**
- یکی **worker/server**

بنابراین طبیعی است که با Kill کردن، بیش از یک PID بسته شود.
