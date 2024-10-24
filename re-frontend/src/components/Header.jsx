// eslint-disable-next-line no-unused-vars
import React from 'react'

const Header = () => {
    return (
        <div >
            <header>

                <nav className="navbar navbar-expand-lg ">
                    <div className="container">
                    <a className="navbar-brand" href="/">Rule Engine</a>
                    <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
                        <span className="navbar-toggler-icon"></span>
                    </button>
                    <div className="collapse navbar-collapse" id="navbarNavAltMarkup">
                        <div className="navbar-nav ">
                            <a className="nav-item nav-link" href="/">Home</a>
                            <a className="nav-item nav-link" href="/create">Create Rule</a>
                            <a className="nav-item nav-link" href="/combine">Combine Rule</a>
                        </div>
                    </div>
                    </div>
                </nav>
            </header>
        </div>
    )
}

export default Header
