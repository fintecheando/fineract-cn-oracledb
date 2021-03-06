/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.mx.fintecheando.fineract.cn.oracledb.util;

public final class JdbcUrlBuilder {

  private final DatabaseType type;
  private String host;
  private String port;
  private String instanceName;

  private JdbcUrlBuilder(final DatabaseType type) {
    super();
    this.type = type;
  }

  public static JdbcUrlBuilder create(final DatabaseType type) {
    return new JdbcUrlBuilder(type);
  }

  public JdbcUrlBuilder host(final String host) {
    this.host = host;
    return this;
  }

  public JdbcUrlBuilder port(final String port) {
    this.port = port;
    return this;
  }

  public JdbcUrlBuilder instanceName(final String instanceName) {
    this.instanceName = instanceName;
    return this;
  }

  public String build() {
    final String[] hostList = this.host.split(",");
    switch (this.type) {
      case ORACLEDB:
        final StringBuilder jdbcUrl = new StringBuilder();
        final String jdbcProtocol = this.type.prefix();
        jdbcUrl.append(jdbcProtocol);
        for (int i = 0; i < hostList.length; i++) {
          jdbcUrl.append("(ADDRESS=(PROTOCOL=TCP)(HOST=").append(hostList[i].trim()).append(")(PORT=").append(this.port).append("))");          
        }
        if (this.instanceName != null && this.instanceName != " " && hostList.length == 1) {
          jdbcUrl.append(")").append("(CONNECT_DATA=(SID=").append(this.instanceName).append(")(SERVER=DEDICATED))");
        } 
        else if (this.instanceName == null && hostList.length == 1){
          jdbcUrl.append(")").append("(CONNECT_DATA=(SERVER=DEDICATED))");   
        }     
        jdbcUrl.append((hostList.length > 1 ? "(FAILOVER=on)(LOAD_BALANCE=ON))(CONNECT_DATA=(SERVER=DEDICATED)(SID="+this.instanceName+")))" : ")"));
        return jdbcUrl.toString();
      default:
        throw new IllegalArgumentException("Unknown database type '" + this.type.name() + "'");
    }
  }

  public enum DatabaseType {
    ORACLEDB("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=");

    private final String prefix;

    DatabaseType(final String prefix) {
      this.prefix = prefix;
    }

    String prefix() {
      return this.prefix;
    }
  }
}
