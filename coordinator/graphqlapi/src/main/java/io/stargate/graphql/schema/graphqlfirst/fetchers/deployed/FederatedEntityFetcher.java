/*
 * Copyright The Stargate Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.stargate.graphql.schema.graphqlfirst.fetchers.deployed;

import com.apollographql.federation.graphqljava._Entity;
import graphql.schema.DataFetchingEnvironment;
import io.stargate.auth.UnauthorizedException;
import io.stargate.db.datastore.ResultSet;
import io.stargate.db.query.builder.BuiltCondition;
import io.stargate.db.schema.Keyspace;
import io.stargate.graphql.schema.graphqlfirst.processor.EntityModel;
import io.stargate.graphql.schema.graphqlfirst.processor.MappingModel;
import io.stargate.graphql.web.StargateGraphqlContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Executes the {@code _entities} query of GraphQL federation.
 *
 * @see <a
 *     href="https://www.apollographql.com/docs/federation/federation-spec/#resolve-requests-for-entities">The
 *     Apollo Federation spec</a>
 */
public class FederatedEntityFetcher extends DeployedFetcher<List<FederatedEntity>> {

  private final MappingModel mappingModel;

  public FederatedEntityFetcher(MappingModel mappingModel) {
    super(mappingModel);
    this.mappingModel = mappingModel;
  }

  @Override
  protected List<FederatedEntity> get(
      DataFetchingEnvironment environment, StargateGraphqlContext context)
      throws UnauthorizedException {

    List<FederatedEntity> result = new ArrayList<>();
    for (Map<String, Object> representation :
        environment.<List<Map<String, Object>>>getArgument(_Entity.argumentName)) {
      result.add(getEntity(representation, context));
    }
    return result;
  }

  private FederatedEntity getEntity(
      Map<String, Object> representation, StargateGraphqlContext context)
      throws UnauthorizedException {
    Object rawTypeName = representation.get("__typename");
    if (!(rawTypeName instanceof String)) {
      throw new IllegalArgumentException(
          "Entity representations must contain a '__typename' string field");
    }
    String entityName = (String) rawTypeName;
    EntityModel entityModel = mappingModel.getEntities().get(entityName);

    if (entityModel == null) {
      throw new IllegalArgumentException(String.format("Unknown entity type %s", entityName));
    }
    Keyspace keyspace = context.getDataStore().schema().keyspace(entityModel.getKeyspaceName());
    List<BuiltCondition> whereConditions =
        bindWhere(
            entityModel.getPrimaryKeyWhereConditions(),
            representation::containsKey,
            representation::get,
            entityModel::validateNoFiltering,
            keyspace);
    ResultSet resultSet =
        query(entityModel, whereConditions, Optional.empty(), DEFAULT_PARAMETERS, context);
    Map<String, Object> entity = toSingleEntity(resultSet, entityModel);
    return FederatedEntity.wrap(entityModel, entity);
  }
}
