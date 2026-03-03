#!/usr/bin/env python3
"""
Create a simple icon for MediaStar Remote Control
Requires: pip install Pillow
"""

from PIL import Image, ImageDraw, ImageFont
import os

def create_icon():
    # Create a 256x256 image
    size = 256
    img = Image.new('RGBA', (size, size), (26, 26, 46, 255))
    draw = ImageDraw.Draw(img)
    
    # Draw TV shape
    tv_color = (15, 52, 96, 255)
    draw.rectangle([48, 64, 208, 160], fill=tv_color)
    
    # Draw inner screen
    screen_color = (22, 33, 62, 255)
    draw.rectangle([56, 72, 200, 152], fill=screen_color)
    
    # Draw signal waves
    wave_color = (0, 173, 181, 255)
    draw.arc([64, 32, 112, 64], 180, 360, fill=wave_color, width=3)
    draw.arc([80, 24, 128, 56], 180, 360, fill=wave_color, width=2)
    
    # Draw text
    try:
        # Try to use Arial font
        font = ImageFont.truetype("arial.ttf", 24)
        small_font = ImageFont.truetype("arial.ttf", 12)
    except:
        # Fallback to default font
        font = ImageFont.load_default()
        small_font = ImageFont.load_default()
    
    text_color = (255, 255, 255, 255)
    draw.text((110, 180), "MS", fill=text_color, font=font, anchor="mm")
    
    # Draw AI symbol
    ai_color = (0, 173, 181, 255)
    draw.ellipse([188, 44, 212, 68], fill=ai_color)
    draw.text((200, 56), "AI", fill=text_color, font=small_font, anchor="mm")
    
    # Create ICO file with multiple sizes
    sizes = [(16, 16), (32, 32), (48, 48), (64, 64), (128, 128), (256, 256)]
    icons = []
    
    for size_tuple in sizes:
        resized = img.resize(size_tuple, Image.Resampling.LANCZOS)
        icons.append(resized)
    
    # Save as ICO
    img.save("icon.ico", format='ICO', sizes=sizes)
    print("icon.ico created successfully!")
    
    # Also save as PNG for preview
    img.save("icon_preview.png", format='PNG')
    print("icon_preview.png created for preview!")

if __name__ == "__main__":
    try:
        create_icon()
    except ImportError:
        print("Pillow library not found. Install with: pip install Pillow")
    except Exception as e:
        print(f"Error creating icon: {e}")
