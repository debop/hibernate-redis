/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.redis.lucene;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;

import java.io.IOException;

/**
 * org.redis.lucene.RedisDirectory
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 3. 오후 8:58
 */
@Slf4j
public class RedisDirectory extends Directory {

    @Override
    public String[] listAll() throws IOException {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean fileExists(String name) throws IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long fileModified(String name) throws IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void touchFile(String name) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteFile(String name) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long fileLength(String name) throws IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public IndexOutput createOutput(String name) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public IndexInput openInput(String name) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
