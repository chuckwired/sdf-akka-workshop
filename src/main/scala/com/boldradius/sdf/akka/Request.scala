package com.boldradius.sdf.akka

  case class Request(sessionId: Long, timestamp: Long, url: String, referrer: String, browser: String)

object Visit {
  def fromRequests(requests: List[Request]): List[Visit] = {
    requests.map(req => {
      if(requests.last != req){
        Visit(req, requests(requests.indexOf(req) + 1).timestamp - req.timestamp)
      } else {
        Visit(req, 0)
      }
    }
    )
  }
}

case class Visit(request: Request, duration: Long)