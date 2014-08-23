#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ${package}.manager;

/**
 * Marshall Peters
 * Date: 8/15/14
 */
public interface WorkBlock {

    /**
     * Produces a description of the WorkBlock; buildFromResponse() must be able to build an identical WorkBlock from
     * this description.
     *
     * @return the description of the WorkBlock
     */
    String createResponse();

    /**
     * Takes a description of a WorkBlock and makes this WorkBlock equivalent to the described one
     *
     * @param response the description of a WorkBlock
     */
    void buildFromResponse(String response);
}