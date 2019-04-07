import React from 'react';

export default () => {
    return (
        <div
            style={{
                borderBottom: '2px solid #20A7CA',
                align: 'center',
                height: '43px'
            }}>
            <img
                src={require('../../img/x-logo.svg')}
                style={{
                    height: '40px',
                    verticalAlign: 'top'
                }}
                tabIndex={0}
            />

            <div
                style={{
                    color: '$medium-grey',
                    display: 'inline-block'
                }}
            >
                DataStax<br/> Graph Schema Viewer
            </div>

        </div>
    );
}
