<center><h1>PullJira HowTo</h1></center>


### Índice

1. [Overview](#overview)
2. [Obter Chromedriver](#obter-chromedriver)
4. [Ajustar shell script](#shell-script)

# Overview

Este **HowTo** descreve como configurar e utlizar o aplicativo **PullJira** que insere no ABSGP as linhas de registro de horário obtidas do Jira via API.

O aplicativo foi escrito em Java e precisa de um ambiente Java (JRE 8 ou maior) para executar.

# Obter Chromedriver

Precisa baixar o Chromedriver no link abaixo e depois colocá-lo num dos diretórios que façam parte do seu PATH (p.ex. /usr/local/bin)

Não esqueça de torná-lo executável através de ***chmod +x chromedriver***

Teste invocando ***chromedriver*** na linha de comando.

**Importante obter a versão do Chromedriver de acordo com a versão do seu navegador Chrome**.

[ChromeDriver - WebDriver for Chrome](http://chromedriver.chromium.org/downloads)

# Shell Script

Edite e salve o script ajustando as variáveis;

Mude as permissões do script para evitar que outra pessoa possa ver suas credenciais com ***chmod 700 pulljira.sh***

Invoque o aplicativo da seguinte forma:
```
    ./pulljira.sh

```
