#ifndef _COMPARATOR_H_
#define _COMPARATOR_H_

#include <boost/shared_ptr.hpp>
#include <vector>

#include "kleisli.h"
#include "type_list.h"

template<typename T, typename C>
class comparator: public category::kleisli::arr<boost::shared_ptr<T>, boost::shared_ptr<C> > {
    typedef boost::shared_ptr<T> T_ptr;
    typedef boost::shared_ptr<C> C_ptr;
    typedef boost::shared_ptr< category::kleisli::end<C_ptr> > Cont;
    typedef std::vector< std::pair<T_ptr, Cont> > vector;
    vector _values;
public:
    void next(const T_ptr& t) {
        for (typename vector::iterator it = _values.begin(); it != _values.end(); ++it) {
            C_ptr r = it->first->compare(t);
            if (r) {
                it->second->next(r);
                return;
            }
        }
        Cont cont = category::kleisli::sink<C_ptr>::continuation().clone();
        if (SUPER_SUB_CLASS(C, T)) {
            cont->next(t);
        }
        _values.push_back(std::make_pair(t, cont));
    }
    void stop() {
        for (typename vector::iterator it = _values.begin(); it != _values.end(); ++it) {
            it->second->stop();
        }
        _values.clear();
    }
    boost::shared_ptr< category::kleisli::end<T_ptr> > clone() const {
        return boost::make_shared< comparator<T, C> >(*this);
    }
};

#endif
