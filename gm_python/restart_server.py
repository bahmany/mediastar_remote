import subprocess
import sys
import os
import time

# Kill existing Python processes
print("Stopping existing server...")
subprocess.run(['taskkill', '/F', '/IM', 'python.exe'], capture_output=True)
time.sleep(2)

# Start new server
print("Starting server with new changes...")
os.chdir(r'D:\projects\gmscreen\gm_python')
subprocess.run([sys.executable, '-m', 'uvicorn', 'app.main:app', '--reload', '--host', '127.0.0.1', '--port', '8000', '--log-level', 'info'])
