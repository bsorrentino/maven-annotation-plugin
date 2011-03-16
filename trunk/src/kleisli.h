#ifndef _ARR_H_
#define _ARR_H_

namespace category {
namespace kleisli {

template<typename T> T& The() { static T r; return r; }
template<typename T> T& The(const T& t) { static T r(t); return r; }
template<template<typename S> class R, typename S> R<S>& The(const S& s) { static R<S> r(s); return r; }
template<template<typename S, typename T> class R, typename S, typename T> R<S, T>& The(const T& t) { static R<S, T> r(t); return r; }
template<template<typename S, typename T, typename U> class R, typename S, typename T, typename U> R<S, T, U>& The(const U& u) { static R<S, T, U> r(u); return r; }

template<typename S>
struct end {
    virtual void next(const S&) {}
};

template<typename F, typename S>
struct end< std::pair<F, S> > {
    virtual void next(const F&, const S&) {}
};

template<typename V>
void operator>>=(const V& from, end<V>& to) {
    to.next(from);
}

template<typename Iter, typename V>
void operator>>=(const std::pair<Iter, Iter>& from, end<V>& to) {
    for (Iter it = from.first; it != from.second; to.next(*it++)) ;
}

template<typename Funct, typename V>
void operator>>=(const std::pair<Funct, int>& from, end<V>& to) {
    for (int i = 0; i < from.second; ++i) {
        to.next(from.first());
    }
}

template<typename S, typename T>
class arr: public end<S> {
    end<T>* e;
public:
    arr(): e(&The< end<T> >()) {}
    void operator()(const T& t) {
        e->next(t);
    }
    virtual end<S>& operator>>=(end<T>& to) {
        e = &to;
        return *this;
    }
};

template<typename T>
class arr<T, T>: public end<T> {
    end<T>* e;
public:
    arr(): e(&The< end<T> >()) {}
    void next(const T& t) {
        e->next(t);
    }
    void operator()(const T& t) {
        e->next(t);
    }
    virtual end<T>& operator>>=(end<T>& to) {
        e = &to;
        return *this;
    }
};

template<typename S, typename F, typename T>
class arr<S, std::pair<F, T> >: public end<S> {
    end< std::pair<F, T> >* e;
public:
    arr(): e(&The< end< std::pair<F, T> > >()) {}
    void operator()(const F& f, const T& t) {
        e->next(f, t);
    }
    virtual end<S>& operator>>=(end< std::pair<F, T> >& to) {
        e = &to;
        return *this;
    }
};

template<typename F, typename T>
class arr< std::pair<F, T>, std::pair<F, T> >: public end< std::pair<F, T> > {
    end< std::pair<F, T> >* e;
public:
    arr(): e(&The< end< std::pair<F, T> > >()) {}
    void next(const F& f, const T& t) {
        e->next(f, t);
    }
    void operator()(const F& f, const T& t) {
        e->next(f, t);
    }
    virtual end< std::pair<F, T> >& operator>>=(end< std::pair<F, T> >& to) {
        e = &to;
        return *this;
    }
};

template<typename V, typename Iter>
class iterator_end: public end<V> {
    Iter it;
public:
    iterator_end(const Iter& i): it(i) {}
    void next(const V& value) { *it++ = value; }
};

template<typename F, typename S, typename Iter>
class iterator_end<std::pair<F, S>, Iter>: public end< std::pair<F, S> > {
    Iter it;
public:
    iterator_end(const Iter& i): it(i) {}
    void next(const F& f, const S& s) { *it++ = make_pair(f, s); }
};

}}

#endif
