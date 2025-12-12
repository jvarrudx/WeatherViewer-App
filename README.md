# ☁️ WeatherViewer App

Trabalho prático da disciplina de **Programação III**, desenvolvido com base no Capítulo 7 do livro "Android for Programmers", adaptado para consumir uma API REST personalizada hospedada na AWS.

## 🧑🏻‍🎓 Integrante
* **Aluno:** João Victor Costa Arruda
* **Professor:** Eduardo Henrique Marques Ferreira
* **Curso:** Sistemas de Informação - 6° Período
* **Disciplina:** Programação III
  
## 🎯 Visão Geral do Projeto
Este aplicativo consome um Web Service RESTful de previsão do tempo e apresenta as informações de maneira clara e acessível ao usuário. Ao contrário do exemplo do livro (que utiliza OpenWeatherMap), este trabalho foi adaptado para:

- Conectar-se a uma API específica fornecida pelo professor (hospedada na AWS).  
- Trabalhar com um JSON simplificado que contém um array `days`.  
- Mostrar ícones meteorológicos usando Emojis em texto(⛅), evitando download de imagens.  


---

## 💻 Recursos Principais
- **Pesquisa por cidade** — Permite inserir o nome da cidade no formato `Cidade, UF, PAÍS` (ex.: `Sao Paulo, SP, BR`) para obter a previsão.   
- **Chamadas assíncronas** — Utiliza `AsyncTask` para realizar requisições de rede sem bloquear a UI.   
- **Lista customizada de dias** — Cada item exibe:
  - Emoji representando o clima;
  - Data e descrição;
  - Temperatura mínima e máxima (°C);
  - Umidade relativa do ar.
- **Confirmação do local** — Exibe o nome oficial da cidade retornado pela API.

---

## 📂 Estrutura do JSON
A API retorna um objeto contendo um array chamado `days`. Cada elemento possui a seguinte estrutura:

```json
{
    "city": "Passos, MG, BR",
     "days": [
     {
     "date": "2025-11-26",
     "minTempC": 20.5,
     "maxTempC": 28.9,
     "description": "Céu parcialmente nublado",
     "humidity": 0.75,
     "icon": "⛅"
}

## 📱 Print da Aplicação



