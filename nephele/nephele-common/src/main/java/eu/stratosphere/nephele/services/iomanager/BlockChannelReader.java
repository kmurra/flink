/***********************************************************************************************************************
 *
 * Copyright (C) 2010 by the Stratosphere project (http://stratosphere.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/

package eu.stratosphere.nephele.services.iomanager;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import eu.stratosphere.nephele.services.memorymanager.MemorySegment;


/**
 *
 *
 */
public class BlockChannelReader extends BlockChannelAccess<ReadRequest, LinkedBlockingQueue<MemorySegment>>
{
	
	
	protected BlockChannelReader(Channel.ID channelID, RequestQueue<ReadRequest> requestQueue,
			LinkedBlockingQueue<MemorySegment> returnSegments)
	throws IOException
	{
		super(channelID, requestQueue, returnSegments, false);
	}
	

	
	/**
	 * Reads a 
	 * @param segment
	 * @throws IOException
	 */
	public void readBlock(MemorySegment segment) throws IOException
	{
		// check the error state of this channel
		checkErroneous();
		
		// write the current buffer and get the next one
		this.requestsNotReturned.incrementAndGet();
		if (this.closed || this.requestQueue.isClosed()) {
			// if we found ourselves closed after the counter increment,
			// decrement the counter again and do not forward the request
			this.requestsNotReturned.decrementAndGet();
			throw new IOException("The reader has been closed.");
		}
		this.requestQueue.add(new SegmentReadRequest(this, segment));
	}

}


