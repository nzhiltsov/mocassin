ErrorDocuments <- function(logs){
  require(stringr)
  require(plyr)
  error.entries <- 
    subset(logs, Level == "ERROR" &
    Logger == "r.k.n.c.m.u.d.server.MathnetAdapter" &
    regexpr("Failed to handle a document", Message) > 0)
  
  docId <- ldply(error.entries$Message, function(s) str_match(s, "ivm[0-9]+"))
  colnames(docId)[1] <- "docId"
  
  r <- rownames(error.entries)
  error.messages <- ldply(r, function(s) {i <- as.numeric(s) + 1; logs[i,]$Date})
  colnames(error.messages)[1] <- "Message"
  
  GetReason <- function(message){
    reason = NA
    if (regexpr("the arxmliv script failed", message) > 0) {
      reason = "latexml"
    } else if (regexpr("Not normal output while compiling PDF", message) > 0) {
      reason = "pdflatex"
    } else if (regexpr("ru.ksu.niimm.cll.mocassin.parser.pdf.PdflatexCompilationException: expectj.TimeoutException: Timeout waiting for spawn to finish", message) > 0) {
      reason = "pdflatex_timeout"
    } else if (regexpr("java.lang.NoClassDefFoundError: org/eclipse", message) > 0 |
      regexpr("java.lang.NoClassDefFoundError: net/sourceforge/texlipse", message) > 0) {
      reason = "latex"
    } else if (regexpr("Failed to patch a Latex document, because it does not exist", message) > 0) {
      reason = "not_exists"  
    }
    
    reason
  }
  reasons <- ldply(error.messages$Message, GetReason)
  colnames(reasons)[1] <- "Reason"
  as.data.frame(cbind(docId, reasons, error.messages), stringsAsFactors = F)
}