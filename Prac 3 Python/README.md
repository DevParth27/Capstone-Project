# Phishing Website Detection using Machine Learning

A straightforward machine learning project to detect phishing websites using URL features.

## Features

- URL feature extraction (length, special characters, suspicious keywords, etc.)
- Random Forest classifier for detection
- Easy-to-use interface
- Model saving and loading
- Interactive testing

## Installation

1. Install required packages:
```bash
pip install -r requirements.txt
```

## Usage

### Basic Usage
```bash
python phishing_detector.py
```

### Interactive Demo
```bash
python example_usage.py
```

### Using as a Module
```python
from phishing_detector import PhishingDetector

detector = PhishingDetector()
# Train with your data
detector.train(urls, labels)

# Predict
result = detector.predict('http://suspicious-site.com')
print(result)
```

## Features Extracted

- URL length
- Number of special characters (dots, hyphens, etc.)
- Suspicious keywords count
- IP address detection
- HTTPS usage
- Domain length
- Number of subdomains

## Model Performance

The model uses Random Forest classifier and typically achieves good accuracy on the sample dataset.