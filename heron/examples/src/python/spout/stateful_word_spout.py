# Copyright 2016 Twitter. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""module for example spout: WordSpout"""

from itertools import cycle
from collections import Counter
from heron.api.src.python import Spout, StatefulComponent

class StatefulWordSpout(Spout, StatefulComponent):
  """StatefulWordSpout: emits a set of words repeatedly"""
  # output field declarer
  outputs = ['word']

  # pylint: disable=attribute-defined-outside-init
  def initState(self, stateful_state):
    self.recovered_state = stateful_state
    self.logger.info("Recovered state")
    self.logger.info(str(self.recovered_state))

  def preSave(self, checkpoint_id):
    # Purely for debugging purposes
    for (k, v) in self.counter.items():
      self.recovered_state.put(k, v)
    self.logger.info("Checkpoint %s" % checkpoint_id)
    self.logger.info(str(self.recovered_state))

  def initialize(self, config, context):
    self.logger.info("In initialize() of WordSpout")
    self.words = cycle(["hello", "bye", "good", "bad", "heron", "storm"])
    self.counter = Counter()

    self.emit_count = 0
    self.ack_count = 0
    self.fail_count = 0

    self.logger.info("Component-specific config: \n%s" % str(config))
    self.logger.info("Context: \n%s" % str(context))

  def next_tuple(self):
    word = next(self.words)
    self.emit([word], tup_id='message id')
    self.counter[word] += 1
    self.emit_count += 1
    if self.emit_count % 100000 == 0:
      self.logger.info("Emitted " + str(self.emit_count))