ReadLogs <- function(filepath) {
  logs <- 
    read.table(filepath,
               sep='\t', fill=T, header=F,
               col.names=c("Date", "Thread", "Level", "Logger", "None", "Message"),
               stringsAsFactors=F)
logs
}