from SECEdgar.crawler import SecCrawler

filings = SecCrawler()
filings.filing_10Q("MSFT", "0000789019", "20190101", 100)