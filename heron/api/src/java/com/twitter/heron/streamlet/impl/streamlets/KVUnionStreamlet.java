//  Copyright 2017 Twitter. All rights reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package com.twitter.heron.streamlet.impl.streamlets;

import java.util.Set;

import com.twitter.heron.api.topology.TopologyBuilder;
import com.twitter.heron.streamlet.KeyValue;
import com.twitter.heron.streamlet.impl.KVStreamletImpl;
import com.twitter.heron.streamlet.impl.operators.UnionOperator;

/**
 * UnionStreamlet is a Streamlet composed of all the elements of two
 * parent streamlets.
 */
public class KVUnionStreamlet<K, V> extends KVStreamletImpl<K, V> {
  private KVStreamletImpl<K, V> left;
  private KVStreamletImpl<? extends K, ? extends V> right;

  public KVUnionStreamlet(KVStreamletImpl<K, V> left,
                          KVStreamletImpl<? extends K, ? extends V> right) {
    this.left = left;
    this.right = right;
    setNumPartitions(left.getNumPartitions());
  }

  @Override
  public boolean doBuild(TopologyBuilder bldr, Set<String> stageNames) {
    if (!left.isBuilt() || !right.isBuilt()) {
      // We can only continue to build us if both of our parents are built.
      // The system will call us again later
      return false;
    }
    if (getName() == null) {
      setName(defaultNameCalculator("kvunion", stageNames));
    }
    if (stageNames.contains(getName())) {
      throw new RuntimeException("Duplicate Names");
    }
    stageNames.add(getName());
    bldr.setBolt(getName(), new UnionOperator<KeyValue<K, V>>(),
        getNumPartitions()).shuffleGrouping(left.getName()).shuffleGrouping(right.getName());
    return true;
  }
}
