/*
 * Copyright 2010 VoidSearch.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.voidsearch.data.test;

import com.voidsearch.data.provider.facebook.SimpleGraphAPIClient;
import com.voidsearch.data.provider.facebook.objects.*;
import org.testng.annotations.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;

public class SimpleGraphAPIClientTest {

    // sample token - expires every hour, obtained from links at :
    // http://developers.facebook.com/docs/api
    //
    // (for testing only - you should obtain a proper access token)
    //

    public String fbToken = "2227470867|2.7lsiCQ8_bWloy2L94B8vnA__.3600.1272232800-736212683|-bThDZvFpVP2SefvOibwP3hQ2es.";

    @Test
    public void nullTest() {


        try {

            BufferedWriter out = new BufferedWriter(new FileWriter("sample_dataset.csv"));
            out.write("num_likes,num_photos,num_groups\n");

            SimpleGraphAPIClient client = new SimpleGraphAPIClient(fbToken);
            LinkedList<FacebookUser> friends = client.getFriends();

            for (FacebookUser friend : friends) {

                LinkedList<LikedEntry> likes = client.getLikes(friend.getID());
                LinkedList<PhotoEntry> photos = client.getPhotos(friend.getID());
                LinkedList<GroupEntry> groups = client.getGroups(friend.getID());
                out.write(likes.size() + "," + photos.size() + "," + groups.size() + "\n");

            }

            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }

}
