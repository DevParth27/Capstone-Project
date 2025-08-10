import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
from sklearn.preprocessing import LabelEncoder
import joblib
import urllib.parse
import re
from collections import Counter

class PhishingDetector:
    def __init__(self):
        self.model = RandomForestClassifier(n_estimators=100, random_state=42)
        self.label_encoder = LabelEncoder()
        
    def extract_features(self, url):
        """Extract features from URL for phishing detection"""
        features = {}
        
        # URL length
        features['url_length'] = len(url)
        
        # Number of dots
        features['num_dots'] = url.count('.')
        
        # Number of hyphens
        features['num_hyphens'] = url.count('-')
        
        # Number of underscores
        features['num_underscores'] = url.count('_')
        
        # Number of slashes
        features['num_slashes'] = url.count('/')
        
        # Number of question marks
        features['num_question_marks'] = url.count('?')
        
        # Number of equal signs
        features['num_equal_signs'] = url.count('=')
        
        # Number of at symbols
        features['num_at_symbols'] = url.count('@')
        
        # Number of ampersands
        features['num_ampersands'] = url.count('&')
        
        # Number of exclamation marks
        features['num_exclamation'] = url.count('!')
        
        # Number of spaces
        features['num_spaces'] = url.count(' ')
        
        # Number of tildes
        features['num_tildes'] = url.count('~')
        
        # Number of commas
        features['num_commas'] = url.count(',')
        
        # Number of plus signs
        features['num_plus'] = url.count('+')
        
        # Number of asterisks
        features['num_asterisks'] = url.count('*')
        
        # Number of hash symbols
        features['num_hash'] = url.count('#')
        
        # Number of dollar signs
        features['num_dollar'] = url.count('$')
        
        # Number of percent signs
        features['num_percent'] = url.count('%')
        
        # Check for suspicious keywords
        suspicious_keywords = ['secure', 'account', 'update', 'confirm', 'login', 'signin', 'bank', 'paypal', 'ebay', 'amazon']
        features['suspicious_keywords'] = sum(1 for keyword in suspicious_keywords if keyword in url.lower())
        
        # Check for IP address
        ip_pattern = r'\b(?:[0-9]{1,3}\.){3}[0-9]{1,3}\b'
        features['has_ip'] = 1 if re.search(ip_pattern, url) else 0
        
        # Check for HTTPS
        features['has_https'] = 1 if url.startswith('https://') else 0
        
        # Domain length
        try:
            parsed_url = urllib.parse.urlparse(url)
            domain = parsed_url.netloc
            features['domain_length'] = len(domain)
        except:
            features['domain_length'] = 0
            
        # Number of subdomains
        try:
            domain_parts = domain.split('.')
            features['num_subdomains'] = len(domain_parts) - 2 if len(domain_parts) > 2 else 0
        except:
            features['num_subdomains'] = 0
            
        return features
    
    def prepare_dataset(self, urls, labels):
        """Prepare dataset by extracting features from URLs"""
        feature_list = []
        for url in urls:
            features = self.extract_features(url)
            feature_list.append(features)
        
        df = pd.DataFrame(feature_list)
        return df, labels
    
    def train(self, urls, labels):
        """Train the phishing detection model"""
        print("Extracting features from URLs...")
        X, y = self.prepare_dataset(urls, labels)
        
        print("Splitting dataset...")
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
        
        print("Training model...")
        self.model.fit(X_train, y_train)
        
        print("Evaluating model...")
        y_pred = self.model.predict(X_test)
        accuracy = accuracy_score(y_test, y_pred)
        
        print(f"Accuracy: {accuracy:.4f}")
        print("\nClassification Report:")
        print(classification_report(y_test, y_pred))
        
        return accuracy
    
    def predict(self, url):
        """Predict if a URL is phishing or legitimate"""
        features = self.extract_features(url)
        feature_df = pd.DataFrame([features])
        prediction = self.model.predict(feature_df)[0]
        probability = self.model.predict_proba(feature_df)[0]
        
        return {
            'url': url,
            'prediction': 'Phishing' if prediction == 1 else 'Legitimate',
            'confidence': max(probability)
        }
    
    def save_model(self, filename='phishing_model.pkl'):
        """Save the trained model"""
        joblib.dump(self.model, filename)
        print(f"Model saved as {filename}")
    
    def load_model(self, filename='phishing_model.pkl'):
        """Load a pre-trained model"""
        self.model = joblib.load(filename)
        print(f"Model loaded from {filename}")

def main():
    # Sample dataset (in real scenario, you'd load from a CSV file)
    sample_urls = [
        # Legitimate URLs
        'https://www.google.com',
        'https://www.github.com',
        'https://www.stackoverflow.com',
        'https://www.wikipedia.org',
        'https://www.microsoft.com',
        'https://www.apple.com',
        'https://www.amazon.com',
        'https://www.facebook.com',
        'https://www.twitter.com',
        'https://www.linkedin.com',
        
        # Phishing-like URLs (simulated)
        'http://secure-paypal-update.com/login',
        'https://amazon-security-alert.net/confirm',
        'http://192.168.1.1/bank-login',
        'https://paypal-verification.suspicious-domain.com',
        'http://bank-account-update.fake-site.org',
        'https://secure-login-ebay.malicious.com',
        'http://confirm-account-amazon.phishing.net',
        'https://update-paypal-security.fake.org',
        'http://bank-alert-signin.suspicious.com',
        'https://account-verification-paypal.malware.net'
    ]
    
    # Labels: 0 = Legitimate, 1 = Phishing
    sample_labels = [0] * 10 + [1] * 10
    
    # Create and train the detector
    detector = PhishingDetector()
    
    print("Training Phishing Detection Model...")
    print("=" * 50)
    
    accuracy = detector.train(sample_urls, sample_labels)
    
    # Save the model
    detector.save_model()
    
    print("\n" + "=" * 50)
    print("Testing with new URLs:")
    print("=" * 50)
    
    # Test with new URLs
    test_urls = [
        'https://www.google.com/search',
        'http://secure-bank-login.suspicious.com',
        'https://github.com/user/repo',
        'http://paypal-update-account.fake.org'
    ]
    
    for url in test_urls:
        result = detector.predict(url)
        print(f"URL: {result['url']}")
        print(f"Prediction: {result['prediction']}")
        print(f"Confidence: {result['confidence']:.4f}")
        print("-" * 30)

if __name__ == "__main__":
    main()