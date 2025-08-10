import pandas as pd
import requests
from io import StringIO

class DatasetHandler:
    def __init__(self):
        self.data = None
    
    def create_sample_dataset(self, filename='phishing_dataset.csv'):
        """Create a sample dataset for demonstration"""
        # Sample data with more realistic examples
        data = {
            'url': [
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
                'https://www.youtube.com',
                'https://www.netflix.com',
                'https://www.reddit.com',
                'https://www.instagram.com',
                'https://www.pinterest.com',
                
                # Phishing URLs (simulated examples)
                'http://secure-paypal-update.com/login',
                'https://amazon-security-alert.net/confirm',
                'http://192.168.1.1/bank-login',
                'https://paypal-verification.suspicious-domain.com',
                'http://bank-account-update.fake-site.org',
                'https://secure-login-ebay.malicious.com',
                'http://confirm-account-amazon.phishing.net',
                'https://update-paypal-security.fake.org',
                'http://bank-alert-signin.suspicious.com',
                'https://account-verification-paypal.malware.net',
                'http://secure-bank-login.phishing-site.com',
                'https://paypal-account-limited.fake.org',
                'http://amazon-account-suspended.malicious.net',
                'https://ebay-security-notice.suspicious.com',
                'http://bank-verification-required.phishing.org'
            ],
            'label': [0] * 15 + [1] * 15  # 0 = Legitimate, 1 = Phishing
        }
        
        df = pd.DataFrame(data)
        df.to_csv(filename, index=False)
        print(f"Sample dataset created: {filename}")
        return df
    
    def load_dataset(self, filename):
        """Load dataset from CSV file"""
        try:
            self.data = pd.read_csv(filename)
            print(f"Dataset loaded: {filename}")
            print(f"Shape: {self.data.shape}")
            return self.data
        except FileNotFoundError:
            print(f"File {filename} not found. Creating sample dataset...")
            return self.create_sample_dataset(filename)
    
    def get_urls_and_labels(self):
        """Extract URLs and labels from the dataset"""
        if self.data is None:
            raise ValueError("No dataset loaded. Please load a dataset first.")
        
        urls = self.data['url'].tolist()
        labels = self.data['label'].tolist()
        return urls, labels
    
    def dataset_info(self):
        """Display dataset information"""
        if self.data is None:
            print("No dataset loaded.")
            return
        
        print("Dataset Information:")
        print(f"Total URLs: {len(self.data)}")
        print(f"Legitimate URLs: {sum(self.data['label'] == 0)}")
        print(f"Phishing URLs: {sum(self.data['label'] == 1)}")
        print("\nSample URLs:")
        print(self.data.head())

if __name__ == "__main__":
    handler = DatasetHandler()
    dataset = handler.load_dataset('phishing_dataset.csv')
    handler.dataset_info()