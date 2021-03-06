# chan-fp

chan-fp uses [core.async](https://github.com/clojure/core.async) channels to provide an alternate implementation of futures and promises, based on ["From Events to Futures and Promises and back"](http://www.home.hs-karlsruhe.de/~suma0002/publications/events-to-futures.pdf), for Clojure.

This is my first Clojure project, therefore, I'd be grateful for any help or advice.

## Usage

### Futures

Futures are read-only, asynchronous computations that can be read many times. In order to use the provided combinators, a future must always enclose a `Comp` which is a record containing the value of the computation (`:value`) as well as a boolean flag (`:ok`) that indicates whether something went wrong.

Futures can be used in the following manner:

```clojure
(require '[chan-fp.core :as cfp])

(defn my-comp []
  (Thread/sleep 1000)
  (cfp/->Comp 42 true))
;;=> #'user/my-comp

(def my-fut (cfp/future my-comp))                           ; Not blocking.
;;=> #'user/my-fut

(cfp/get my-fut)                                            ; Might blocking.
;;=> #chan_fp.core.Comp{:value 42, :ok true}
```

The following set of combinators is currently implemented (`doc` will be available ASAP):

* `on-success`
* `on-failure`
* `or-else`
* `when`
* `then`
* `any`

Additionally, since futures are channels, one can basically use every function that operations on channels (checkout the [core.async API](http://clojure.github.io/core.async/)). For instance:

```clojure
(require '[chan-fp.core :as cfp]
         '[clojure.core.async :as async])

(let [fut (cfp/future (fn []
                        (Thread/sleep 2000)
                        (cfp/->Comp 3 true)))]
  (async/alt!!
    fut ([comp] (:value comp))
    (async/timeout 1000) :timeout))
;;=> :timeout
```

### Promises

TODO

## License

Copyright © 2016 Daniel Kraus

Distributed under the Eclipse Public License version 1.0.
