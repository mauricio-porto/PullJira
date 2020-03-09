<center><h1>PullJira HowTo</h1></center>


### Índice

1. [Overview](#overview)
2. [Obter Chromedriver](#obter-chromedriver)
3. [Exportar dados do Jira](#exportar-jira)
4. [Ajustar shell script](#shell-script)

# Overview

Este **HowTo** descreve como configurar e utlizar o aplicativo **PullJira** que insere no ABSGP as linhas de registro de horário obtidas do Jira em formato CSV.

O aplicativo foi escrito em Java e precisa de um ambiente Java (JRE 8 ou maior) para executar.

# Obter Chromedriver

Precisa baixar o Chromedriver no link abaixo e depois colocá-lo num dos diretórios que façam parte do seu PATH (p.ex. /usr/local/bin)

Não esqueça de torná-lo executável através de ***chmod +x chromedriver***

Teste invocando ***chromedriver*** na linha de comando.

**Importante obter a versão do Chromedriver de acordo com a versão do seu navegador Chrome**.

[ChromeDriver - WebDriver for Chrome](http://chromedriver.chromium.org/downloads)

# Exportar Jira

1. Acesse a página de **Reporting** (a partir do menu *Timetracker*) no Jira
2. Selecione o período desejado e clique em **Create Report**
3. Na seleção **Columns** (direita, perto de *Export*) selecione **APENAS**

   ***Issue Key***,

   ***Worklog Start Time*** e

   ***Worklog Time Spent***

4. Então escolha **Export -> (CSV) Current Columns** e será feito o download.

# Shell Script

Coloque o script **pulljira.sh** no mesmo diretório que o arquivo **PullJira.jar**.

Edite e salve o script ajustando as variáveis ***emailUser*** e ***passwordUser*** de acordo com suas credenciais no **ABSGP**

Mude as permissões do script para evitar que outra pessoa possa ver suas credenciais com ***chmod 700 pulljira.sh***

Invoque o aplicativo da seguinte forma:
```
    ./pulljira.sh <worklog-details-xxxx.csv>

```
