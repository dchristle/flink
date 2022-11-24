/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.runtime.metrics.dump;

/** Container for scope related information as required by the MetricQueryService. */
public abstract class QueryScopeInfo {
    /**
     * Categories to be returned by {@link QueryScopeInfo#getCategory()} to avoid instanceof checks.
     */
    public static final byte INFO_CATEGORY_JM = 0;

    public static final byte INFO_CATEGORY_TM = 1;
    public static final byte INFO_CATEGORY_JOB = 2;
    public static final byte INFO_CATEGORY_TASK = 3;
    public static final byte INFO_CATEGORY_OPERATOR = 4;

    /** The remaining scope not covered by specific fields. */
    public final String scope;

    private QueryScopeInfo(String scope) {
        this.scope = scope;
    }

    /**
     * Create a copy of this QueryScopeInfo and append the given scope.
     *
     * @param userScope scope to append
     * @return modified copy of this QueryScopeInfo
     */
    public abstract QueryScopeInfo copy(String userScope);

    /**
     * Returns the category for this QueryScopeInfo.
     *
     * @return category
     */
    public abstract byte getCategory();

    @Override
    public String toString() {
        return "QueryScopeInfo{"
                + "scope='"
                + scope
                + '\''
                + ", category='"
                + getCategory()
                + '\''
                + '}';
    }

    protected String concatScopes(String additionalScope) {
        return scope.isEmpty() ? additionalScope : scope + "." + additionalScope;
    }

    /** Container for the job manager scope. Stores no additional information. */
    public static class JobManagerQueryScopeInfo extends QueryScopeInfo {
        public JobManagerQueryScopeInfo() {
            super("");
        }

        public JobManagerQueryScopeInfo(String scope) {
            super(scope);
        }

        @Override
        public JobManagerQueryScopeInfo copy(String additionalScope) {
            return new JobManagerQueryScopeInfo(concatScopes(additionalScope));
        }

        @Override
        public byte getCategory() {
            return INFO_CATEGORY_JM;
        }
    }

    /** Container for the task manager scope. Stores the ID of the task manager. */
    public static class TaskManagerQueryScopeInfo extends QueryScopeInfo {
        public final String taskManagerId;

        public TaskManagerQueryScopeInfo(String taskManagerId) {
            this(taskManagerId, "");
        }

        public TaskManagerQueryScopeInfo(String taskManagerId, String scope) {
            super(scope);
            this.taskManagerId = taskManagerId;
        }

        @Override
        public TaskManagerQueryScopeInfo copy(String additionalScope) {
            return new TaskManagerQueryScopeInfo(this.taskManagerId, concatScopes(additionalScope));
        }

        @Override
        public byte getCategory() {
            return INFO_CATEGORY_TM;
        }
    }

    /** Container for the job scope. Stores the ID of the job. */
    public static class JobQueryScopeInfo extends QueryScopeInfo {
        public final String jobId;

        public JobQueryScopeInfo(String jobId) {
            this(jobId, "");
        }

        public JobQueryScopeInfo(String jobId, String scope) {
            super(scope);
            this.jobId = jobId;
        }

        @Override
        public JobQueryScopeInfo copy(String additionalScope) {
            return new JobQueryScopeInfo(this.jobId, concatScopes(additionalScope));
        }

        @Override
        public byte getCategory() {
            return INFO_CATEGORY_JOB;
        }
    }

    /** Container for the task scope. Stores the ID of the job/vertex and subtask index. */
    public static class TaskQueryScopeInfo extends QueryScopeInfo {
        public final String jobId;
        public final String vertexId;
        public final int subtaskIndex;
        public final int attemptNumber;

        public TaskQueryScopeInfo(
                String jobId, String vertexId, int subtaskIndex, int attemptNumber) {
            this(jobId, vertexId, subtaskIndex, attemptNumber, "");
        }

        public TaskQueryScopeInfo(
                String jobId, String vertexId, int subtaskIndex, int attemptNumber, String scope) {
            super(scope);
            this.jobId = jobId;
            this.vertexId = vertexId;
            this.subtaskIndex = subtaskIndex;
            this.attemptNumber = attemptNumber;
        }

        @Override
        public TaskQueryScopeInfo copy(String additionalScope) {
            return new TaskQueryScopeInfo(
                    this.jobId,
                    this.vertexId,
                    this.subtaskIndex,
                    this.attemptNumber,
                    concatScopes(additionalScope));
        }

        @Override
        public byte getCategory() {
            return INFO_CATEGORY_TASK;
        }
    }

    /**
     * Container for the operator scope. Stores the ID of the job/vertex, the subtask index and the
     * name of the operator.
     */
    public static class OperatorQueryScopeInfo extends QueryScopeInfo {
        public final String jobId;
        public final String vertexId;
        public final int subtaskIndex;
        public final int attemptNumber;
        public final String operatorName;

        public OperatorQueryScopeInfo(
                String jobId,
                String vertexId,
                int subtaskIndex,
                int attemptNumber,
                String operatorName) {
            this(jobId, vertexId, subtaskIndex, attemptNumber, operatorName, "");
        }

        public OperatorQueryScopeInfo(
                String jobId,
                String vertexId,
                int subtaskIndex,
                int attemptNumber,
                String operatorName,
                String scope) {
            super(scope);
            this.jobId = jobId;
            this.vertexId = vertexId;
            this.subtaskIndex = subtaskIndex;
            this.attemptNumber = attemptNumber;
            this.operatorName = operatorName;
        }

        @Override
        public OperatorQueryScopeInfo copy(String additionalScope) {
            return new OperatorQueryScopeInfo(
                    this.jobId,
                    this.vertexId,
                    this.subtaskIndex,
                    this.attemptNumber,
                    this.operatorName,
                    concatScopes(additionalScope));
        }

        @Override
        public byte getCategory() {
            return INFO_CATEGORY_OPERATOR;
        }
    }
}
