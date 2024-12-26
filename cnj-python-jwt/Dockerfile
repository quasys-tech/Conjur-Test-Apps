# Base image olarak Python kullan
FROM python:3.9-slim

# Gerekli araçları yükleyin
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       curl \
    && rm -rf /var/lib/apt/lists/*

# Çalışma dizinini ayarla
WORKDIR /app

# Gerekli Python paketlerini yükle
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Uygulama kodunu konteynere kopyala
COPY . .


# Uygulamayı çalıştır
CMD ["python", "-u", "secret.py"]


