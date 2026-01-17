#!/bin/bash

# Скрипт для створення Kubernetes Secrets
# Запускати ОДИН раз перед першим деплоєм
#
# Використання:
#   ./create-secrets.sh
#
# Або з власними паролями:
#   POSTGRES_PASSWORD=mypass MONGO_PASSWORD=mypass ./create-secrets.sh

set -e

echo "=== Створення Kubernetes Secrets ==="

# Значення за замовчуванням (для розробки)
POSTGRES_USER="${POSTGRES_USER:-postgres}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-$(openssl rand -base64 16)}"
MONGO_USER="${MONGO_USER:-admin}"
MONGO_PASSWORD="${MONGO_PASSWORD:-$(openssl rand -base64 16)}"
GIT_TOKEN="${GIT_TOKEN:-}"

# PostgreSQL Secret
echo "Створюю postgresql-secret..."
kubectl create secret generic postgresql-secret \
  --from-literal=username="$POSTGRES_USER" \
  --from-literal=password="$POSTGRES_PASSWORD" \
  --dry-run=client -o yaml | kubectl apply -f -

# MongoDB Secret
echo "Створюю mongodb-secret..."
kubectl create secret generic mongodb-secret \
  --from-literal=username="$MONGO_USER" \
  --from-literal=password="$MONGO_PASSWORD" \
  --dry-run=client -o yaml | kubectl apply -f -

# Config Server Secret (якщо потрібен GitHub token)
if [ -n "$GIT_TOKEN" ]; then
  echo "Створюю config-server-secret..."
  kubectl create secret generic config-server-secret \
    --from-literal=git-token="$GIT_TOKEN" \
    --dry-run=client -o yaml | kubectl apply -f -
fi

echo ""
echo "=== Секрети створено ==="
echo ""
echo "PostgreSQL:"
echo "  Username: $POSTGRES_USER"
echo "  Password: $POSTGRES_PASSWORD"
echo ""
echo "MongoDB:"
echo "  Username: $MONGO_USER"
echo "  Password: $MONGO_PASSWORD"
echo ""
echo "ВАЖЛИВО: Збережіть ці паролі у безпечному місці!"
echo ""
echo "Перевірка: kubectl get secrets"
