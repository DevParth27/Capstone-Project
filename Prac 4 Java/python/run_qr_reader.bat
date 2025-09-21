@echo off
echo Installing required packages...
pip install -r requirements.txt

echo Running QR Code Reader...
python qr_code_app.py
pause