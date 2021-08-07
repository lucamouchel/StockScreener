# StockViewer Application
## Monitor investments, portfolio or simply view stocks information from american markets in real time or in the past.

### Features
- Home Page:
  When first running the app, three main sections can be distinguished, these consist of:
  - Trending stocks with basic information such as current value or difference from previous day close 
  - News in american financial markets with links to the articles on the Web.
  - Your own investments in the stock market you have to manually add.
- Searching for stocks in the search bar
  - A auto-completing field allows the user to find stocks more easily.
  - When pressing enter once a stock is selected will remove the news panel and replace it with a grey pane with in-depth information on the stock chosen as well as a chart with the stock's evolution.
  - You can pick the range on the chart: 1 month, 3 months, 6 months, 1 year, 2 years, 5 years, Max.
- Portfolio
  - From the home page, you can edit and create your portfolio with stocks you are interested in following.
- Others
  - Forex: Open a new pane and select a currency and amount to convert into another currency
  - Crypto-currencies: Clicking on the button will allow you to type the name of a cryptocurrency and open a website with in-depth information.
  - Non-american stocks: The libraries used only allow to search for american stocks so this button will allow to search for whichever stock and will lead the user to a webpage with in-depth information on the stock.

![Homepage-noEdits](https://user-images.githubusercontent.com/73081373/128522529-f95fd6ea-5252-4824-8717-e3d66cf7d0c4.png) View on the home page when first running the application.



### Your investments and portfolio.
- To add an investment on the app, a user can simply enter the symbol or company name and the number of shares bought at X price (you have to write the number of shares *x* the value at the time of purchase ie 20 shares at 10.56$ would be written 20x10.56):
  - When adding an investment, by using the HMTLUnit library, the program will access a google web page searching for the stock chosen and will pick out a precise html element from the page source containing the current stock value. From there, simple calculations can be made from the numbers of shares bought at X price.
- For the portfolio, you can click on add a stock and you can enter the company name or symbol and it will add it to the menu button.
  - When pressing on a stock in the portfolio, the news pannel is removed and replaced by informations on the clicked stock.
- When a stock is added in the portfolio or an investment is recorded, it is written to a .txt file so when re-running the application, every information will appear again.
  - I will maybe convert the .txt files to .json in the nearby future.
 
### How most of it works
For this project, I used various libraries and APIs. The most frequent API I used is Yahoo Finance which provides in-depth information on most of american stocks. In fact the supported stocks for the auto-completion field come from the NYSE, NASDAQ and AMEX exchanges. However, having used free plans which have limited API calls, I also used the Alpha Vantage API. Regarding the libraries, I mostly used: JSON, HTMLUnit, Selenium and JavaFx of course. JSON was used to filter the data incoming from the APIs and Html unit to find certain html elements in the webpage when adding an investment. I used selenium to automate the search for non-american stocks or cryptocurrencies. In fact, a user just needs to type a european stock for example and it will open a yahoo finance web page that will search for the given stock automatically and open it.
  - Selenium will complete the textfield on the webpage with the symbol entered and from the suggested quotes, will select the one most similar to the symbol entered and click on it.
  - The same principle is used for cryptocurrencies. If a wrong crypto is entered, selenium will lead you back to the homepage of the website.

### GUI
I used FXML and scene builder to create most of the GUI for this project.
 
### Run the app
The app uses JavaFX and FXML so the following VM options have to be added into the run configurations (in intellij) : 
--module-path **path.to.javafx.LIB** --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.base --add-exports=javafx.base/com.sun.javafx.event=ALL-UNNAMED

Annex:
Example of 3 different investments (the first one consist of **36 shares bought at the price of 13.74**) and clicking on a stock in your portfolio
![image](https://user-images.githubusercontent.com/73081373/128526372-ff5d7c25-60f2-4808-87c5-894e77a070d7.png)



 
