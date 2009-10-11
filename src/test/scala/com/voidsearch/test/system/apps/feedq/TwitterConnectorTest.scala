package com.voidsearch.test.system.apps.feedq

import org.testng.annotations._
import voidbase.apps.feedq.connector.twitter.TwitterGeoFeedFetcher
import voidbase.storage.jobqueue.{SimpleJobResult, JobResult, JobRequest, JobQueue}
/**
 * simple twitter connector test
 *
 * @author Aleksandar Bradic
 *
 */
class TwitterConnectorTest {

  @Test
  def nullTest() = {
    var jobQueue = new JobQueue
    var req = new SimpleTwitterRequest
    jobQueue.put(req);
    println(jobQueue.get(req))
  }

  class SimpleTwitterRequest extends JobRequest {
    def execute(): JobResult = {
      var result = new SimpleJobResult
      var fetcher = new TwitterGeoFeedFetcher
      result.setResult(fetcher.fetch())
      return result
    }

    def expired(): JobResult = {
      return null;
    }
    
  }


}