#See https://aka.ms/customizecontainer to learn how to customize your debug container and how Visual Studio uses this Dockerfile to build your images for faster debugging.

FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY ["cnjr-netcore-jwt/cnjr-netcore-jwt.csproj", "cnjr-netcore-jwt/"]
RUN dotnet restore "cnjr-netcore-jwt/cnjr-netcore-jwt.csproj"
COPY . .
WORKDIR "/src/cnjr-netcore-jwt"
RUN dotnet build "cnjr-netcore-jwt.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "cnjr-netcore-jwt.csproj" -c Release -o /app/publish /p:UseAppHost=false
RUN dotnet dev-certs https --clean
RUN dotnet dev-certs https --trust

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
# ADD conjur-follower.crt  /usr/local/share/ca-certificates/conjur-follower.crt
# RUN chmod 644 /usr/local/share/ca-certificates/conjur-follower.crt && update-ca-certificates
ENTRYPOINT ["dotnet", "cnjr-netcore-jwt.dll"]