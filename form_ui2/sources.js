(function(app) {
    app.directive('dgState', function() {
        return {
            scope: {
                state: '=dgState',
                nameCheck: '&',
                removeState: '&',
                allStates: '&'
            },
            templateUrl: 'state.tpl.html',
            controller: function($scope) {

                $scope.stateKey = 'values';
                $scope.newEntry = function() {
                    if($scope.name && $scope.expression && $scope.nameCheck({name:$scope.name})) {
                        $scope.state[$scope.stateKey].push({name:$scope.name, expression: $scope.expression});
                        $scope.name = '';
                        $scope.expression = '';
                    }
                }
                $scope.removeEntry = function(entry) {
                    var index = $scope.state[$scope.stateKey].indexOf(entry);
                    $scope.state[$scope.stateKey].splice(index, 1);
                }
            }
        }
    });

    app.directive('dgEntry', function() {
        return {
            templateUrl: 'entry.tpl.html'
        }
    });

    app.directive('dgForm', function(dgServices) {
        return {
            templateUrl: 'form.tpl.html',
            controller: function($scope) {
                $scope.states = [];
                var cc =1;
                $scope.addState = function() {
                    var setv = 'SETV' + $scope.states.length;
                    var newEntry = {};
                    newEntry.values = [];
                    newEntry.name = 'SETV'+ cc++
                    $scope.states.push(newEntry);
                    console.log($scope.states);
                }
                $scope.checkNameTaken = function(name) {
                    var result = true;
                    angular.forEach($scope.states, function(state) {
                        angular.forEach(state, function(values, key) {
                            angular.forEach(values, function(val) {
                                if(angular.equals(val.name, name)) {
                                    result = false;
                                }
                            },this);
                        },this);
                    },this);
                    return result;
                };
                $scope.removeState = function(state) {
                    var index = $scope.states.indexOf(state);
                    $scope.states.splice(index, 1);
                };
                $scope.getAllStateNames = function(notKey) {
                    var result = [];
                    if(!angular.equals('start', notKey)){
                        result.push('end');
                    }
                    angular.forEach($scope.states, function(state) {
                        if(!angular.equals(notKey, state.name)) {
                            result.push(state.name);
                        }
                    });
                    return result;
                }

                $scope.transform = function() {
                    dgServices.transform($scope.startTransition, $scope.states);
                }
            }
        };
    })
    app.service('dgServices', function($http) {

        var appendTransition = function(event, target) {
            var transition = document.createElement('transition');
            transition.setAttribute('event', event);
            transition.setAttribute('target', target);
            return (transition);
        }

        var appendState = function(state) {
            var stateNode = document.createElement('state');
            stateNode.setAttribute('id', state.name);
            var onEntry = document.createElement('onentry');
            angular.forEach(state.values, function(value) {
                var assign = document.createElement('assign');
                assign.setAttribute('name', value.name);
                assign.setAttribute('expression', value.expression);
                onEntry.appendChild(assign);
            });
            if(state.values && state.values.length > 0) {
                stateNode.appendChild(onEntry);
            }
            if(state.transition) {
                stateNode.appendChild(appendTransition(state.transition, state.transition));
            }
            return stateNode;
        }

        this.transform = function(start, states) {
            var node = document.createElement("div");
            var xml = document.createElement('scxml');
            xml.setAttribute('xmlns', "http://www.w3.org/2005/07/scxml");
            xml.setAttribute('xmlns:cs', "http://commons.apache.org/scxml");
            xml.setAttribute('version', '1.0');
            xml.setAttribute('initial', 'start');

            xml.appendChild(appendState({name:'start', transition:start}));

            angular.forEach(states, function(state) {
                xml.appendChild(appendState(state));
            });

            node.appendChild(xml);
            console.log(node.innerHTML);
        }
    })

}(angular.module('dg.form', [])));