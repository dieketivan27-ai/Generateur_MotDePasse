@echo off
echo [DEVOPS] Lancement de l'architecture distribuée...
echo [1/3] Nettoyage des anciens conteneurs...
docker compose down
echo [2/3] Construction et démarrage des conteneurs (Generator et Scorer )...
docker compose up --build -d

echo [3/3] Connexion à l'application Java...
echo -------------------------------------------------------
docker attach password-generator
echo -------------------------------------------------------

echo [FIN] Arrêt des services (mais les conteneurs restent visibles)...
docker compose stop
echo.
echo Vous pouvez maintenant voir les conteneurs et leurs logs dans Docker Desktop.
pause
