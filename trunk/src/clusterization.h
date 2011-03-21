#ifndef _CLUSTERIZATION_H_
#define _CLUSTERIZATION_H_

#include <boost/shared_ptr.hpp>
#include <typeinfo>
#include <map>

#include "kleisli.h"

template<typename T>
class clusterization: public category::kleisli::arr<boost::shared_ptr<T>, boost::shared_ptr<T> > {
    typedef boost::shared_ptr<T> T_ptr;
    struct less {
        bool operator()(const T_ptr& t1, const T_ptr& t2) {
            return typeid(*t1).before(typeid(*t2));
        }
    };
    typedef boost::shared_ptr<category::kleisli::end<T_ptr> > Cont;
    std::map<T_ptr, Cont, less> _continuations;
public:
    void next(const T_ptr& t) {
        typename std::map<T_ptr, Cont, less>::iterator it = _continuations.find(t);
        if (it == _continuations.end()) {
            Cont cont = category::kleisli::sink<T_ptr>::continuation().clone();
            _continuations.insert(std::make_pair(t, cont));
            cont->next(t);
        } else {
            it->second->next(t);
        }
    }
};

#endif
