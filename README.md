# chan-fp

chan-fp uses [core.async](https://github.com/clojure/core.async) channels to provide an alternate implementation of futures and promises, based on ["From Events to Futures and Promises and back"](http://www.home.hs-karlsruhe.de/~suma0002/publications/events-to-futures.pdf), for Clojure.

## Usage

### Futures

Futures are read-only, asynchronous computations that can be read many times. In order to use the provided combinators, a future must enclose a `Comp` which is a record containing the value of the computation (`:value`) and a boolean flag (`:ok`) that indicates whether something went wrong. `Comp` implements `IDeref`, hence, the value can be obtained via `deref` (or `@`) as well.

Futures can be used in the following manner:

```clojure
(require '[chan-fp.core :as cfp])

(defn my-comp []
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

Additionally, since futures are channels, one can basically use every function that operations on channels (checkout the [official API](http://clojure.github.io/core.async/)). For instance:

```clojure
(require '[chan-fp.core :as cfp]
         '[clojure.core.async :as async])

(let [fut (cfp/future (fn []
                        (Thread/sleep 1000)
                        (cfp/->Comp 1 true)))]
  (async/alt!!
    fut ([comp] @comp)
    (async/timeout 2000) :timeout))
;;=> 1
```

This is my first Clojure project, therefore, I'd be grateful for any help or advice.

### Promises

FIXME

## License

Copyright © 2016 Daniel Kraus

Distributed under the Eclipse Public License version 1.0.
